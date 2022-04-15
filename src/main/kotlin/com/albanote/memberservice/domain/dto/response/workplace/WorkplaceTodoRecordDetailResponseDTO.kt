package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

class WorkplaceTodoRecordDetailResponseDTO @QueryProjection constructor(
    val todoRecordId: Long? = null,
    val completedTime: LocalTime? = null,
    val todoDate: LocalDate? = null,
    val memo: String? = null
) {
    var todo: WorkplaceTodoDetailResponseDTO? = null
}