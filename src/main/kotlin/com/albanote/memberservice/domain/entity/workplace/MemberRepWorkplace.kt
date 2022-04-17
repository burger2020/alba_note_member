package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.member.Member
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "member_rep_workplace_id"))
class MemberRepWorkplace(
    id: Long? = null,

    @JoinColumn(name = "workplace_id")
    @OneToOne(fetch = FetchType.LAZY)
    val workplace: Workplace? = null,

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    val member: Member? = null
) : BaseEntity(id) {
}