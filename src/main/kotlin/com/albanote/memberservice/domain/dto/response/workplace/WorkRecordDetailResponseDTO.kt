package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection

/**
 * 근무 상세 기록 조회 요청 DTO
 * */
class WorkRecordDetailResponseDTO @QueryProjection constructor(
    val workRecordId: Long,

) {
}