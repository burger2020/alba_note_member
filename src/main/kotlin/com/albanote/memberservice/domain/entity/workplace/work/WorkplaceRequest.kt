package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.EmployeeMemberRank
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_request_id"))
class WorkplaceRequest(
    id: Long? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val workplace: Workplace? = null,

    @JoinColumn(name = "employee_member_rank_id")
    @OneToOne(fetch = FetchType.LAZY)
    val requestEmployeeMemberRank: EmployeeMemberRank? = null,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = false)
    val requestType: WorkplaceRequestType? = null,

    // 요청 근무 날짜
    val requestWorkDate: LocalDate? = null,

    // 근무 정정시 정정 요청한 근무
    @JoinColumn(name = "work_record_id", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    val requestWorkRecord: WorkRecord? = null,

    // 정정 및 등록 시간
    val requestOfficeGoingTime: LocalTime? = null,
    val requestQuittingTime: LocalTime? = null,
    val requestBreakTime: LocalTime? = null,
    val requestNightBreakTime: LocalTime? = null,

    // 요청 내용
    @Column(columnDefinition = "TEXT", nullable = true)
    val requestContent: String? = null,

    // 사장님 메모
    @Column(columnDefinition = "TEXT", nullable = true)
    val memo: String? = null,

    // 요청 결과
    @Column(nullable = true)
    val isCompleted: Boolean? = null,

    val requestTotalSalary: Int? = null
) : BaseTimeEntity(id) {
}