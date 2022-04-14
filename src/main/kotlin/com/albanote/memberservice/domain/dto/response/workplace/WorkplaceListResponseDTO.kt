package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection

class WorkplaceListResponseDTO @QueryProjection constructor(
    var workplaceId: Long,
    var workplaceTitle: String,
    var isRep: Boolean
) {
}