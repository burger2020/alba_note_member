package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

/**
 * 현재 근무자 조회 DTO
 * */
class CurrentEmployeeResponseDTO @QueryProjection constructor(
    val currentEmployee: EmployeeMemberSimpleResponseDTO,
    val officeGoingTime: LocalTime,
)
