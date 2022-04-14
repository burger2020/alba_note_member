package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection

class EmployeeMemberSimpleResponseDTO @QueryProjection constructor(
    var memberId: Long,
    var name: String,
    var imageUrl: String?,
    var rankName: String
) {
}