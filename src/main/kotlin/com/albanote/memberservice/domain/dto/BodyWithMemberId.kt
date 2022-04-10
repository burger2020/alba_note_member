package com.albanote.memberservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

class BodyWithMemberId<T>(
    @JsonProperty("body")
    val body: T,
    val memberId: Long
)