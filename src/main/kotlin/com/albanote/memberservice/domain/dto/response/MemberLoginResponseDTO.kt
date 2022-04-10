package com.albanote.memberservice.domain.dto.response

import com.albanote.memberservice.domain.entity.member.MemberType
import com.albanote.memberservice.domain.entity.member.OsType
import com.albanote.memberservice.domain.entity.member.SocialLoginType
import com.querydsl.core.annotations.QueryProjection

class MemberLoginResponseDTO @QueryProjection constructor(
    var id: Long? = null,
    val socialId: String? = null,
    val socialLoginType: SocialLoginType? = null,
    val osType: OsType? = null,
    val memberType: MemberType? = null,
) {
    var memberTokenInfo: MemberTokenResponseDTO? = null
}