package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection

/**
 * 일터 목록 조회 응답 DTO
 * */
class WorkplaceListResponseDTO @QueryProjection constructor(
    var workplaceId: Long,
    var workplaceTitle: String,
    var workplaceImageUrl: String?
) {
    var isRep: Boolean = false
}