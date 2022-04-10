package com.albanote.memberservice.domain.dto.response

class MemberTokenResponseDTO(
    val accessToken: String,
    val refreshToken: String?
)