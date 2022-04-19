package com.albanote.memberservice.domain.dto.response.workplace

import com.albanote.memberservice.domain.entity.workplace.work.WorkType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 근무 상세 기록 조회 요청 DTO
 * */
class WorkRecordDetailResponseDTO @QueryProjection constructor(
    val workRecordId: Long,
    val employeeMember: EmployeeMemberSimpleResponseDTO,
    val workType: WorkType,
    val workedDate: LocalDate,
    val officeGoingTime: LocalTime?,
    val quittingTime: LocalTime?,
    val memo: String,
    val hourlyWage: Int
) {
    val officeHours: Int = 0
    val isHoliday: Boolean = false
    val overtimeHours: Int = 0
    val nightWorkedHours: Int = 0
}