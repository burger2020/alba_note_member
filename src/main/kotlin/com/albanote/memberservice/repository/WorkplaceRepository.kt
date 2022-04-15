package com.albanote.memberservice.repository

import com.albanote.memberservice.domain.dto.query.workplace.QWorkTodayEmployeeDTO
import com.albanote.memberservice.domain.dto.query.workplace.QWorkplaceTodoDTO
import com.albanote.memberservice.domain.dto.query.workplace.WorkplaceTodoDTO
import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.domain.entity.workplace.QEmployeeMember.employeeMember
import com.albanote.memberservice.domain.entity.workplace.QEmployeeRank.employeeRank
import com.albanote.memberservice.domain.entity.workplace.QWorkplace.workplace
import com.albanote.memberservice.domain.entity.workplace.work.QEmployeeTodo.employeeTodo
import com.albanote.memberservice.domain.entity.workplace.work.QTodo.todo
import com.albanote.memberservice.domain.entity.workplace.work.QTodoRecord.todoRecord
import com.albanote.memberservice.domain.entity.workplace.work.QWorkRecord.workRecord
import com.albanote.memberservice.domain.entity.workplace.work.QWorkplaceRequest.workplaceRequest
import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType.*
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class WorkplaceRepository : RepositorySupport() {

    /** 홈화면 대표 일터 정보 조회 **/
    fun findMyWorkplaceInfo(memberId: Long, workplaceId: Long?): WorkplaceInfoOfBossResponseDTO? {
        return select(
            QWorkplaceInfoOfBossResponseDTO(
                workplace.id,
                workplace.title
            )
        ).from(employeeMember)
            .innerJoin(employeeMember.workplace, workplace)
            .on(if (workplaceId != null) workplace.id.eq(workplaceId) else workplace.isRep.isTrue)
            .where(employeeMember.member.id.eq(memberId))
            .fetchFirst()
    }

    /** 오늘 완료한 할 일 조회 **/
    fun findWorkplaceTodoRecordsByDate(
        workplaceId: Long,
        isComplete: Boolean,
        pageable: Pageable,
        date: LocalDate = LocalDate.now()
    ): List<TodoRecordResponseDTO> {
        // 오늘 어제 완료된 할 일 조회
        val completedTodos = select(
            QTodoRecordResponseDTO(
                todoRecord.id,
                todo.id,
                todo.title,
                todoRecord.completedTime,
                todoRecord.todoDate,
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.member.id,
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                )
            )
        ).from(todoRecord)
            .innerJoin(todoRecord.todo, todo)
            .innerJoin(todoRecord.employeeMember, employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(
                todoRecord.workplace.id.eq(workplaceId),
                todoRecord.todoDate.eq(date).or(
                    todoRecord.todoDate.eq(date.minusDays(1)).and(
                        todo.startTime.gt(todo.endTime)
                    )
                )
            )
            .pageableOption(pageable)
            .fetch()
            .toMutableList()

        if (!isComplete) {
            // 일터에 등록된 할일 조회
            val todos = select(
                QWorkplaceTodoDTO(
                    todo.id,
                    todo.title,
                    todo.todoCycleType,
                    todo.todoDays,
                    todo.startTime,
                    todo.endTime
                )
            )
                .from(todo)
                .where(
                    todo.workplace.id.eq(workplaceId),
                    // 오늘만 할 일일 경우 생성 날짜가 어제 또는 오늘(마감시간이 당일 넘거갈 수 있으므로)
                    todo.todoCycleType.eq(TODAY)
                        .and(
                            // 어제 등록 된
                            todo.createDate.eq(LocalDate.now().minusDays(1))
                                .and(
                                    todo.startTime.isNotNull
                                        .and(todo.endTime.isNotNull)
                                        .and(todo.startTime.gt(todo.endTime))
                                ).or(todo.createDate.eq(LocalDate.now()))
                        )
                        .or( // 또는 생성 날짜가 오늘 이전인것들만
                            todo.todoCycleType.ne(TODAY).and(todo.createDate.goe(date.minusDays(1)))
                        )
                )
                .fetch()

            val dayOfWeek = LocalDate.now().dayOfWeek.value
            val dayOfMonth = LocalDate.now().dayOfMonth
            val dayTodos = todos.filter {
                it.todoCycleType == DAILY && it.startTime != null && it.endTime != null && it.endTime!! < it.startTime
            }.toMutableList()
            val todayTodos = todos.filter {
                // 오늘 또는 어제 할 일 필터
                when (it.todoCycleType) {
                    WEEKLY -> weeklyAndMonthlyTodoFilterCondition(it, dayOfWeek, dayTodos)
                    MONTHLY -> weeklyAndMonthlyTodoFilterCondition(it, dayOfMonth, dayTodos)
                    else -> true
                }
            }.toMutableList()
            todayTodos.addAll(dayTodos)

            // 미완료, 완료 한 리스트에 합치기
            val completedTodoIds = completedTodos.map { it.todoId }
            val uncompletedTodos = todos.filter { todo ->
                if (completedTodoIds.contains(todo.todoId)) {
                    val todoRecord = completedTodos.filter { it.todoId == it.todoId }
                    if (todo.isToday) {
                        todoRecord.firstOrNull { it.completedDate == LocalDate.now() } == null
                    } else if (!todo.isToday) {
                        todoRecord.firstOrNull { it.completedDate == LocalDate.now().minusDays(1) } == null
                    } else true
                } else true
            }.map { it.convertToUncompletedTodoRecord() }
            completedTodos.addAll(uncompletedTodos)
        }
        return completedTodos
    }

    // 주간, 월간 할 일 필터 조건
    fun weeklyAndMonthlyTodoFilterCondition(
        todo: WorkplaceTodoDTO,
        dayOf: Int,
        dayTodos: MutableList<WorkplaceTodoDTO>
    ): Boolean {
        return when {
            todo.todoDays?.substring(dayOf - 2, dayOf) == "11" &&
                    todo.startTime != null && todo.endTime != null && todo.endTime!! < todo.startTime -> {
                dayTodos.add(todo.copyWithIdToday(false))
                true
            }
            todo.todoDays?.substring(dayOf - 1, dayOf) == "1" -> {
                todo.isToday = true
                true
            }
            todo.todoDays?.substring(dayOf - 2, dayOf - 1) == "1" -> {
                todo.isToday = false
                todo.startTime != null && todo.endTime != null && todo.endTime!! < todo.startTime
            }
            else -> false
        }
    }

    /** 오늘 해야할 할일 개수 조회 **/
    fun findWorkplaceTodayTotalTodoCount(workplaceId: Long): Int {
        val day = LocalDate.now().dayOfWeek.value
        val date = LocalDate.now().dayOfMonth
        return select(todo.id)
            .from(todo)
            .where(
                todo.workplace.id.eq(workplaceId),
                todo.todoCycleType.eq(DAILY).or(
                    todo.todoCycleType.eq(WEEKLY).and(todo.todoDays.substring(day - 1, day).eq("1"))
                ).or(
                    todo.todoCycleType.eq(MONTHLY).and(todo.todoDays.substring(date - 1, date).eq("1"))
                )
            ).fetch().size
    }

    /** 현재 근무자 조회 **/
    fun findWorkplaceCurrentEmployees(workplaceId: Long): MutableList<WorkRecordResponseDTO> {
        return select(
            QWorkRecordResponseDTO(
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.member.id,
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                ),
                workRecord.officeGoingTime,
                workRecord.quittingTime
            )
        )
            .from(workRecord)
            .innerJoin(workRecord.employeeMember, employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(
                workRecord.workplace.id.eq(workplaceId),
                workRecord.workDate.eq(LocalDate.now()),
                workRecord.officeGoingTime.isNotNull,
                workRecord.quittingTime.isNull,
            ).fetch()
    }

    /** 일터 목록 조회 **/
    fun findWorkplaceListByMember(memberId: Long): List<WorkplaceListResponseDTO> {
        return select(
            QWorkplaceListResponseDTO(
                workplace.id,
                workplace.title,
                workplace.isRep
            )
        )
            .from(employeeMember)
            .innerJoin(employeeMember.workplace, workplace)
            .where(employeeMember.member.id.eq(memberId))
            .fetch()
    }

    /** 일터 요청 조회 **/
    fun findRequestListByWorkplace(workplaceId: Long, pageable: Pageable): List<WorkplaceRequestSimpleResponseDTO> {
        return select(
            QWorkplaceRequestSimpleResponseDTO(
                workplaceRequest.id,
                workplaceRequest.createDate,
                workplaceRequest.requestType,
                workplaceRequest.requestResult,
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.member.id,
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                )
            )
        ).from(workplaceRequest)
            .innerJoin(workplaceRequest.requestEmployeeMember, employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(workplaceRequest.workplace.id.eq(workplaceId))
            .pageableOption(pageable)
            .fetch()
    }

    fun findRequestDetail(requestId: Long): WorkplaceRequestDetailResponseDTO? {
        return select(
            QWorkplaceRequestDetailResponseDTO(
                workplaceRequest.id,
                workplaceRequest.createDate,
                workplaceRequest.requestType,
                workplaceRequest.requestContent,
                workplaceRequest.requestResult,
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.member.id,
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                ),
                workplaceRequest.memo,
                workplaceRequest.requestWorkDate,
                workplaceRequest.correctionOfficeGoingTime,
                workplaceRequest.correctionQuittingTime
            )
        ).from(workplaceRequest)
            .innerJoin(workplaceRequest.requestEmployeeMember, employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(workplaceRequest.id.eq(requestId))
            .fetchFirst()
    }

    /** 일터 할 일 기록 상세 조회 **/
    fun findWorkplaceTodoRecordDetail(todoRecordId: Long?, todoId: Long): WorkplaceTodoRecordDetailResponseDTO {
        // 할 일 상세 조회
        val todoDetail = select(
            QWorkplaceTodoDetailResponseDTO(
                todo.id,
                todo.title,
                todo.startTime,
                todo.endTime,
                todo.todoCycleType,
                todo.todoDays
            )
        ).from(todo)
            .where(todo.id.eq(todoId))
            .fetchFirst()

        // 할 일 담당 직원들 조회
        val chargeEmployee = select(
            QEmployeeMemberSimpleResponseDTO(
                employeeMember.member.id,
                employeeMember.id,
                employeeMember.name,
                employeeMember.imageUrl,
                employeeRank.name
            )
        ).from(employeeTodo)
            .innerJoin(employeeTodo.employeeMember, employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(employeeTodo.todo.id.eq(todoDetail.todoId))
            .fetch()

        todoDetail.chargeEmployee = chargeEmployee

        val todoRecordDetail = if (todoRecordId != null) {
            // 할 일 기록 있으면 기록 상세 조회
            select(
                QWorkplaceTodoRecordDetailResponseDTO(
                    todoRecord.id,
                    todoRecord.completedTime,
                    todoRecord.todoDate,
                    todoRecord.memo
                )
            ).from(todoRecord)
                .where(todoRecord.id.eq(todoRecordId))
                .fetchFirst() ?: WorkplaceTodoRecordDetailResponseDTO()
        } else {
            WorkplaceTodoRecordDetailResponseDTO()
        }
        todoRecordDetail.todo = todoDetail

        return todoRecordDetail
    }

    /** 직원 조회 **/
    fun finEmployeesByWorkplace(
        workplaceId: Long,
        excludeEmployeeIds: List<Long>,
        date: LocalDate? = LocalDate.now()
    ): List<EmployeeMemberSimpleResponseDTO> {
        // 오늘 출근인 직원 전체 조회
        select(
            QWorkTodayEmployeeDTO(
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.member.id,
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                ),
                employeeRank.workingDay
            )
        )
            .from(employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(employeeMember.workplace.id.eq(workplaceId), employeeMember.createDate. loe(date?.atStartOfDay()))
            .fetch()


        return select(
            QEmployeeMemberSimpleResponseDTO(
                employeeMember.member.id,
                employeeMember.id,
                employeeMember.name,
                employeeMember.imageUrl,
                employeeRank.name
            )
        )
            .from(employeeMember)
            .innerJoin(employeeMember.employeeRank, employeeRank)
            .where(
                employeeMember.workplace.id.eq(workplaceId),
                employeeMember.id.notIn(excludeEmployeeIds)
            )
            .fetch()


    }
}


