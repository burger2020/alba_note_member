package com.albanote.memberservice.domain.dto.request.member

import com.albanote.memberservice.NoArgAndAllOpen
import com.albanote.memberservice.domain.entity.member.OsType
import com.albanote.memberservice.domain.entity.member.SocialLoginType
import java.io.Serializable

/**
 * 유저 로그인 요청 DTO
 * */
@NoArgAndAllOpen
class MemberLoginRequestDTO(
    var socialId: String,
    var socialLoginType: SocialLoginType,
    var osType: OsType
) : Serializable