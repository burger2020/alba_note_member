package com.albanote.memberservice.domain.entity.member

import javax.persistence.*

@Entity
@AttributeOverride(name = "fcmToken", column = Column(name = "member_fcm_token"))
class MemberFcmToken(
    @Id
    var fcmToken: String? = null,

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    var member: Member? = null
)