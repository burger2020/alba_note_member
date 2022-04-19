package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseEntity
import java.time.LocalDate
import javax.persistence.*


@Entity
@AttributeOverride(name = "id", column = Column(name = "employee_member_rank_id"))
class EmployeeMemberRank(
    id: Long? = null,

    @Column(nullable = false)
    var createdDate: LocalDate? = null,
    @Column(nullable = true)
    var deprecatedDate: LocalDate? = null,

    @JoinColumn(name = "employee_member_id")
    @OneToOne(fetch = FetchType.LAZY)
    val employeeMember: EmployeeMember,

    @JoinColumn(name = "employee_rank_id")
    @OneToOne(fetch = FetchType.LAZY)
    val employeeRank: EmployeeRank
) : BaseEntity(id) {
}