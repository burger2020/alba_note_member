package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

class WorkplaceRequestCorrectionWorkRecordResponseDTO @QueryProjection constructor(
    var existingOfficeGoingTime: LocalTime,
    var existingQuittingTime: LocalTime,
    var workDate: LocalDate
) {
}