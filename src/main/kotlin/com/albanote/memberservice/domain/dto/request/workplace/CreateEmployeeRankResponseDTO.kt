package com.albanote.memberservice.domain.dto.request.workplace

import com.albanote.memberservice.domain.entity.workplace.EmployeeRank
import com.albanote.memberservice.domain.entity.workplace.Workplace
import com.albanote.memberservice.domain.entity.workplace.work.PayrollCycleType
import com.albanote.memberservice.domain.entity.workplace.work.PayrollType
import com.albanote.memberservice.domain.entity.workplace.work.WorkRecordType
import java.time.LocalTime

/**
 * 직급 생성 요청 DTO
 * */
class CreateEmployeeRankResponseDTO(
    val workplaceId: Long,

    val payrollType: PayrollType,
    val payrollCycle: PayrollCycleType,

    val ordinaryHourlyWage: Int,

    val name: String,
    val salary: Int,
    val hourlyWageCalculationUnit: Int,
    val officeGoingTime: LocalTime?,
    val quittingTime: LocalTime?,
    val breakTime: Int,

    val workingDay: String,
    var isCommuteTimeVaryByDayOfWeek: Boolean,
    val commuteTimeByDateOfWeek: List<CreateCommuteTimeByDayOfWeekResponseDTO>,

    val paidHoliday: String,
    var payday: Int,
    var settlementDate: Int,

    var workRecordType: WorkRecordType,

    var isWorkApprovalRequirement: Boolean,
    var isNightAllowance: Boolean,
    var nightAllowanceExtraMultiples: Float,
    var isOvertimeAllowance: Boolean,
    var overtimeAllowanceExtraMultiples: Float,
    var isHolidayAllowance: Boolean,
    var holidayAllowanceExtraMultiples: Float
) {

    fun convertToEntity(): EmployeeRank {
        val rank = EmployeeRank(
            workplace = Workplace(workplaceId),
            name = name,
            isBoss = false,
            payrollType = payrollType,
            salary = salary,
            payrollCycle = payrollCycle,
            ordinaryHourlyWage = ordinaryHourlyWage,
            hourlyWageCalculationUnit = hourlyWageCalculationUnit,
            officeGoingTime = officeGoingTime,
            quittingTime = quittingTime,
            breakTime = breakTime,
            workingDay = workingDay,
            paidHoliday = paidHoliday,
            payday = payday,
            settlementDate = settlementDate,
            workRecordType = workRecordType,
            isCommuteTimeVaryByDayOfWeek = isCommuteTimeVaryByDayOfWeek,
            isWorkApprovalRequirement = isWorkApprovalRequirement,
            isNightAllowance = isNightAllowance,
            nightAllowanceExtraMultiples = nightAllowanceExtraMultiples,
            isOvertimeAllowance = isOvertimeAllowance,
            overtimeAllowanceExtraMultiples = overtimeAllowanceExtraMultiples,
            isHolidayAllowance = isHolidayAllowance,
            holidayAllowanceExtraMultiples = holidayAllowanceExtraMultiples
        )

        return rank
    }
}