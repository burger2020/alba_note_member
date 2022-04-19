package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import com.albanote.memberservice.domain.entity.member.Member
import com.albanote.memberservice.domain.entity.workplace.work.EmployeeTodo
import com.albanote.memberservice.domain.entity.workplace.work.WorkRecord
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@AttributeOverride(name = "id", column = Column(name = "employee_member_id"))
class EmployeeMember(
    id: Long? = null,

    val leaveDate: LocalDateTime? = null,

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

    @JoinColumn(name = "employee_rank_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var employeeRank: EmployeeRank? = null,

    @OneToMany(mappedBy = "employeeMember")
    var employeeTodo: MutableList<EmployeeTodo> = mutableListOf(),

    @Column(columnDefinition = "TEXT", nullable = false)
    var name: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    var imageUrl: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var phoneNumber: String? = null,

    // 사장님에게만 정보 공개
    val isOpenToBoss: Boolean? = null,

    @OneToMany(mappedBy = "employeeMember")
    val workRecords: MutableList<WorkRecord> = mutableListOf()
) : BaseTimeEntity(id) {

}
