package com.albanote.memberservice.domain.dto.response.workplace

import com.albanote.memberservice.domain.entity.workplace.work.PayrollType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

/**
 * 직원 직급별 급여 정보
 * */
class EmployeeRankSalaryInfoResponseDTO @QueryProjection constructor(
    val rankId: Long,
    var ordinaryHourlyWage: Int?,
    val hourlyWageCalculationUnit: Int?,
    val breakTime: LocalTime?,
    val nightBreakTime: LocalTime?,
    val paidHoliday: String?,
    var isCommuteTimeVaryByDayOfWeek: Boolean?,
    var isNightAllowance: Boolean?,
    var nightAllowanceExtraMultiples: Float?,
    var isOvertimeAllowance: Boolean?,
    var overtimeAllowanceExtraMultiples: Float?,
    var isHolidayAllowance: Boolean?,
    var holidayAllowanceExtraMultiples: Float?,
    var payrollType: PayrollType,
)