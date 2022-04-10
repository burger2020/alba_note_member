package com.albanote.memberservice.repository

import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.domain.entity.workplace.QEmployeeMember.employeeMember
import com.albanote.memberservice.domain.entity.workplace.QEmployeeRank.employeeRank
import com.albanote.memberservice.domain.entity.workplace.QWorkplace.workplace
import com.albanote.memberservice.domain.entity.workplace.work.QTodo.todo
import com.albanote.memberservice.domain.entity.workplace.work.QTodoRecord.todoRecord
import com.albanote.memberservice.domain.entity.workplace.work.QWorkRecord.workRecord
import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType.*
import com.querydsl.core.types.dsl.Expressions
import org.springframework.stereotype.Repository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Repository
class WorkplaceRepository : RepositorySupport() {

    /** 홈화면 대표 일터 정보 조회 **/
    fun findMyWorkplaceInfo(memberId: Long, workplaceId: Long): RepWorkplaceInfoOfBossResponseDTO? {
        return select(
            QRepWorkplaceInfoOfBossResponseDTO(
                Expressions.asNumber(workplaceId),
                workplace.title
            )
        ).from(workplace)
            .innerJoin(workplace.employeeMembers, employeeMember)
            .where(workplace.id.eq(workplaceId))
            .fetchFirst()
    }

    /** 현재 근무자 조회 **/
    fun findWorkplaceTodayCompletedTodo(workplaceId: Long): List<CompletedTodoResponseDTO> {
        return select(
            QCompletedTodoResponseDTO(
                todoRecord.id,
                todo.title,
                todoRecord.CompletionTime
            )
        ).from(todoRecord)
            .innerJoin(todoRecord.todo, todo)
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
                employeeMember.member.id,
                employeeMember.name,
                employeeMember.imageUrl,
                employeeRank.name,
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
}
