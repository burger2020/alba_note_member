package com.albanote.memberservice.domain.dto.response.workplace

import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

class WorkplaceTodoDetailResponseDTO @QueryProjection constructor(
    val todoId: Long,
    val todoTitle: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val todoCycleType: TodoCycleType,
    val todoDays: String,
) {
    var chargeEmployee: List<EmployeeMemberSimpleResponseDTO> = listOf()
}