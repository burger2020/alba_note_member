package com.albanote.memberservice.domain.dto.request.workplace

import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import java.time.LocalTime

/**
 * 일터 할 일 수정 요청 DTO
 *
 * @param todoDays - todoCycle 이 Daily 면 Null
 * */
class ModifyWorkplaceTodoRequestDTO(
    val todoIdToModify: Long,
    val workplaceId: Long,
    val todoTitle: String,
    val todoDescription: String,
    val chargeEmployeeIds: List<Long>,
    val isAuthenticationImageRequire: Boolean,
    val startTime: LocalTime,
    val endTime: LocalTime,
    var todoCycleType: TodoCycleType,
    var todoDays: String
) {
    fun convertToCreateDTO(): CreateWorkplaceTodoRequestDTO {
        return CreateWorkplaceTodoRequestDTO(
            workplaceId = workplaceId,
            todoTitle = todoTitle,
            todoDescription = todoDescription,
            chargeEmployeeIds = chargeEmployeeIds,
            isAuthenticationImageRequire = isAuthenticationImageRequire,
            startTime = startTime,
            endTime = endTime,
            todoCycleType = todoCycleType,
            todoDays = todoDays
        )
    }
}