package com.albanote.memberservice.domain.dto.request.workplace

/**
 * 대표 일터 삭제 요청 DTO
 * */
class DeleteRepWorkplaceRequestDTO(
    val workplaceId: Long,
    val memberId: Long
) {
}