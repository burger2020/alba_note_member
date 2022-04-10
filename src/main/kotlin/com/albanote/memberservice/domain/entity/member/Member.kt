package com.albanote.memberservice.domain.entity.member

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "member_id"))
@EntityListeners(AuditingEntityListener::class)
class Member(
    id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var socialId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = false)
    var socialLoginType: SocialLoginType? = null,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = false)
    var osType: OsType? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val pwd: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    var memberType: MemberType? = null,

    @JoinColumn(name = "fcm_token_id")
    @OneToOne(fetch = FetchType.LAZY)
    val fcmToken: MemberFcmToken? = null
) : BaseTimeEntity(id)