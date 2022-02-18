package com.albanote.memberservice.domain.entity.member

import com.albanote.memberservice.domain.dto.request.member.MemberLoginRequestDTO
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@AttributeOverride(name = "uuid", column = Column(name = "member_uuid"))
@EntityListeners(AuditingEntityListener::class)
class Member(
    @Id
    var uuid: String? = null,

    var id: String? = null,

    @CreatedDate
    var createDate: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val pwd: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var name: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    var nickname: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true, unique = true)
    var imageUrl: String? = null

) {

    constructor(member: MemberLoginRequestDTO, passwordEncoder: BCryptPasswordEncoder) : this(
        uuid = UUID.randomUUID().toString().substring(0..10),
        id = member.id,
        pwd = passwordEncoder.encode(member.pwd),
//        name = member.name,
//        nickname = member.nickname
    )
}