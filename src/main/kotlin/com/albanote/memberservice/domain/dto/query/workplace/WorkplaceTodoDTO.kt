package com.albanote.memberservice.domain.dto.query.workplace

import com.albanote.memberservice.domain.dto.response.workplace.TodoRecordResponseDTO
import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

class WorkplaceTodoDTO @QueryProjection constructor(
    var todoId: Long,
    var todoTitle: String,
    var todoCycleType: TodoCycleType,
    var todoDays: String?,
    var startTime: LocalTime?,
    var endTime: LocalTime?,
) {
    var isToday = true

    fun copyWithIdToday(isToday: Boolean): WorkplaceTodoDTO {
        val copy = WorkplaceTodoDTO(todoId, todoTitle, todoCycleType, todoDays, startTime, endTime)
        copy.isToday = isToday
        return copy
    }

    fun convertToUncompletedTodoRecord(): TodoRecordResponseDTO {
        val todoRecord = TodoRecordResponseDTO(
            null, todoId, todoTitle, null, null, null
        )
        todoRecord.isCompleted = false
        return todoRecord
    }
}