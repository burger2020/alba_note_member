package com.albanote.memberservice.domain.dto.response.workplace

import com.albanote.memberservice.domain.entity.workplace.work.WorkplaceRequestType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime


// 최근 요청
class WorkplaceRequestSimpleResponseDTO @QueryProjection constructor(
    var requestId: Long,
    var createdDate: LocalDateTime,
    var requestType: WorkplaceRequestType,
    var requestResult: Boolean?,
    var requestMember: EmployeeMemberSimpleResponseDTO
)