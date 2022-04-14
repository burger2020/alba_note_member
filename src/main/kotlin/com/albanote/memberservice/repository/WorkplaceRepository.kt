package com.albanote.memberservice.repository

import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.domain.entity.workplace.QEmployeeMember.employeeMember
import com.albanote.memberservice.domain.entity.workplace.QEmployeeRank.employeeRank
import com.albanote.memberservice.domain.entity.workplace.QWorkplace.workplace
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
    fun findWorkplaceTodayCompletedTodo(workplaceId: Long): List<CompletedTodoResponseDTO> {
        return select(
            QCompletedTodoResponseDTO(
                todoRecord.id,
                todo.title,
                todoRecord.CompletionTime,
                QEmployeeMemberSimpleResponseDTO(
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
                todoRecord.todoDate.eq(LocalDate.now())
            )
            .fetch()
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
    fun findWorkplaceCurrentEmployees(workplaceId: Long): List<CurrentEmployeeResponseDTO> {
        return select(
            QCurrentEmployeeResponseDTO(
                QEmployeeMemberSimpleResponseDTO(
                    employeeMember.id,
                    employeeMember.name,
                    employeeMember.imageUrl,
                    employeeRank.name
                ),
                workRecord.officeGoingTime
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
}
