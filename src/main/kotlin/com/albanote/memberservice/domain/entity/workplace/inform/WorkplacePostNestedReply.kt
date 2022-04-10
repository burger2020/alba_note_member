package com.albanote.memberservice.domain.entity.workplace.inform

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import com.albanote.memberservice.domain.entity.member.Member
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_inform_nested_reply_d"))
class WorkplacePostNestedReply(
    id: Long? = null,

    @JoinColumn(name = "created_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val createdMember: Member? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String? = null,

    @JoinColumn(name = "reply_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val reply: WorkplacePostReply? = null

) : BaseTimeEntity(id) {
}