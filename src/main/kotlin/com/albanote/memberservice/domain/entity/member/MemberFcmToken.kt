package com.albanote.memberservice.domain.entity.member

import com.albanote.memberservice.domain.entity.BaseEntity
import javax.persistence.*

@Entity
@AttributeOverride(name = "fcmToken", column = Column(name = "fcm_token_id"))
class MemberFcmToken(
    id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    var fcmToken: String? = null,

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    val member: Member? = null
) : BaseEntity()