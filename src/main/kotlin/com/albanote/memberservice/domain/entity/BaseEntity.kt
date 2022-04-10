package com.albanote.memberservice.domain.entity

import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
) : Serializable