package com.albanote.memberservice.domain.dto.query.workplace

import com.albanote.memberservice.domain.dto.response.workplace.EmployeeMemberSimpleResponseDTO
import com.albanote.memberservice.domain.entity.workplace.CommuteTimeByDayOfWeek
import com.querydsl.core.annotations.QueryProjection

class WorkTodayEmployeeDTO @QueryProjection constructor(
    val employee: EmployeeMemberSimpleResponseDTO,

//    val isCommuteTimeVaryByDayOfWeek: Boolean,
    // 근무 요일
    val workingDay: String
) {
//    val commuteTimeByDateOfWeek: MutableList<CommuteTimeByDayOfWeek> = mutableListOf()
}