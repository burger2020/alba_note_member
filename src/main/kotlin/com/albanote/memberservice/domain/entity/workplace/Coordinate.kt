package com.albanote.memberservice.domain.entity.workplace

import javax.persistence.Embeddable
import javax.persistence.Embedded

// 좌표
@Embeddable
class Coordinate(
    var lat: Float? = null,
    var lng: Float? = null
)