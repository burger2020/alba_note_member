package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.work.PayrollCycleType
import com.albanote.memberservice.domain.entity.workplace.work.PayrollType
import com.albanote.memberservice.domain.entity.workplace.work.WorkRecordType
import java.time.LocalTime
import javax.persistence.*

/**
 * 작급 정보
 * */
@Entity
@AttributeOverride(name = "id", column = Column(name = "employee_rank_id"))
class EmployeeRank(
    id: Long? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

    @OneToMany(mappedBy = "employeeRank")
    var employeeMember: MutableList<EmployeeMember> = mutableListOf(),

    // 직급 이름
    @Column(columnDefinition = "TEXT", nullable = false)
    val name: String? = null,

    // 급여 지급 방식
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    val payrollType: PayrollType? = null,

    /** 월급 직원 정보 **/
    // 월급
    val salary: Int? = null,

    /** 시급 직원 정보 **/
    // 급여 지급 주기
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    val payrollCycle: PayrollCycleType? = null,

    /** 공통 정보 **/
    // 통상 시급(수당 계산 시 계산)
    var ordinaryHourlyWage: Int? = null,
    // 시급 계산 단위 (분)
    val hourlyWageCalculationUnit: Int? = null,
    // 시급 계산 반올림,버림
    val isHourlyWageCalculationRound: Boolean? = null,

    // 출퇴근 시간
    val officeGoingTime: LocalTime? = null,
    val quittingTime: LocalTime? = null,

    // 휴게시간 분단위
    val breakTime: Int? = null,

    // 근무 요일 (MTWTFSS) 출근 안하는날은 _ ex) 월수금일 출근 -> 1_1_1_1
    @Column(columnDefinition = "TEXT", nullable = true)
    val workingDay: String? = null,

    // 유급 요일 (MTWTFSS) 출근하는 날 _ ex) 일 유급 휴일 -> ______1
    @Column(columnDefinition = "TEXT", nullable = true)
    val paidHoliday: String? = null,

    // 급여일 수요일 -> 3, 5일 -> 5
    var payday: Int? = null,

    // 급여 정산일  월 ~ 일 -> 7, 1일 까지 -> 1
    var settlementDate: Int? = null,

    // 근무 지급 방식
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    var workRecordType: WorkRecordType? = null,

    // 출퇴근 시간 요일별로 다른지 여부
    var isCommuteTimeVaryByDayOfWeek: Boolean? = null,
    @OneToMany(mappedBy = "dayOfWeek")
    val commuteTimeByDateOfWeek: MutableList<CommuteTimeByDayOfWeek> = mutableListOf(),

    // 근무 승인 필수 여부
    var isWorkApprovalRequirement: Boolean? = null,
    // 야간 수당
    var isNightAllowance: Boolean? = null,
    // 초과 수당
    var isOvertimeAllowance: Boolean? = null,
    // 휴일 수당
    var isHolidayAllowance: Boolean? = null,
) : BaseEntity(id) {
}
