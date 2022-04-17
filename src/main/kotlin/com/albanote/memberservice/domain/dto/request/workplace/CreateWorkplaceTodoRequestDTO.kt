package com.albanote.memberservice.domain.dto.request.workplace

import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import java.time.LocalTime

/**
 * 일터 할 일 생성 요청 DTO
 *
 * @param todoDays - todoCycle 이 Daily 면 Null
 * */
open class CreateWorkplaceTodoRequestDTO(
    val workplaceId: Long,
    val todoTitle: String,
    val todoDescription: String,
    val chargeEmployeeIds: List<Long>,
    val isAuthenticationImageRequire: Boolean,
    val startTime: LocalTime,
    val endTime: LocalTime,
    var todoCycleType: TodoCycleType,
    var todoDays: String?
) {

}