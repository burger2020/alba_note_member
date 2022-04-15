package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

/**
 * 완료된 할일 조회 DTO
 * */
class CompletedTodoResponseDTO @QueryProjection constructor(
    val todoRecordId: Long,
    val todoTitle: String,
    val completedTime: LocalTime,
    val completedMember: EmployeeMemberSimpleResponseDTO
) {
    val imageUrls: List<String> = listOf()

}
