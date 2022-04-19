package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

/**
 * 완료된 할일 조회 DTO
 * */
class TodoRecordResponseDTO @QueryProjection constructor(
    val todoRecordId: Long?,
    val todoId: Long,
    val todoTitle: String,
    val completedTime: LocalTime?,
    val completedDate: LocalDate?,
    val completedMember: EmployeeMemberSimpleResponseDTO?,
) {
    var isCompleted = true
    val imageUrls: List<String> = listOf()

}
