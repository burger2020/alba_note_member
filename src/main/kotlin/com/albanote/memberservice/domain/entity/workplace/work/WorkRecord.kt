package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.EmployeeMemberRank
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*


@Entity
@AttributeOverride(name = "id", column = Column(name = "work_record_id"))
class WorkRecord(
    id: Long? = null,

    @JoinColumn(name = "employee_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var employeeMember: EmployeeMember? = null,

    @JoinColumn(name = "employee_member_rank_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var employeeRankMember: EmployeeMemberRank? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

// 근무 유형
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    var workType: WorkType? = null,

// 근무 날짜
    val workDate: LocalDate? = null,

// 출퇴근 시간
    val officeGoingTime: LocalTime? = null,
    val quittingTime: LocalTime? = null,

    // 휴게시간
    val breakTime: LocalTime? = null,
    val nightBreakTime: LocalTime? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    val memo: String? = null,

    val totalSalary: Int? = null
) : BaseEntity(id) {

}
