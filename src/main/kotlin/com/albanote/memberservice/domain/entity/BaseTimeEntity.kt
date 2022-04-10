package com.albanote.memberservice.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseTimeEntity(id: Long? = null) : BaseEntity(id) {
    @CreatedDate
    var createDate: LocalDateTime? = null
}