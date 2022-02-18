package com.albanote.memberservice.domain.dto.request.member

import com.albanote.memberservice.NoArgAndAllOpen
import java.io.Serializable

/**
 * 유저 로그인 요청 DTO
 * */
@NoArgAndAllOpen
class MemberLoginRequestDTO(
    var id: String,
    var pwd: String
) : Serializable