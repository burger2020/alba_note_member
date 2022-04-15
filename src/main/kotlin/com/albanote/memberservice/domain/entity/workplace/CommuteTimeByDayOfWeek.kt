package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseEntity
import java.time.LocalTime
import javax.persistence.*

/**
 * 요일별 출퇴근 시간
 * */
@Entity
@AttributeOverride(name = "id", column = Column(name = "employee_rank_id"))
class CommuteTimeByDayOfWeek(
    id: Long? = null,

    @JoinColumn(name = "employee_rank_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val employeeRank: EmployeeRank? = null,

    // 해당 요일 월,화,수 -> 111____
    @Column(columnDefinition = "TEXT", nullable = true)
    val dayOfWeek: String? = null,

    // 출퇴근 시간
    val officeGoingTime: LocalTime? = null,
    val quittingTime: LocalTime? = null,
) : BaseEntity(id) {

}