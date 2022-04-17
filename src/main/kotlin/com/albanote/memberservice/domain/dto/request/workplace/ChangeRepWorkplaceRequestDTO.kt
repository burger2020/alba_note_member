package com.albanote.memberservice.domain.dto.request.workplace

/**
 * 대표 일터 변경
 * */
class ChangeRepWorkplaceRequestDTO(
    val workplaceId: Long,
    val memberId: Long
) {
}