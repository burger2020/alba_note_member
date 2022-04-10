package com.albanote.memberservice.domain.entity.workplace.inform

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.member.Member
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_inform_reply_like_id"))
class WorkplacePostNestedReplyLike(
    id: Long? = null,


    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member? = null,

    @JoinColumn(name = "workplace_inform_reply_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val workplaceInformReply: WorkplacePostReply? = null,

    val isLike: Boolean? = null,

    ) : BaseEntity(id) {
}