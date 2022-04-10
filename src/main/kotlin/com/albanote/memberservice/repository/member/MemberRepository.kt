package com.albanote.memberservice.repository.member

import com.albanote.memberservice.domain.dto.response.MemberLoginResponseDTO
import com.albanote.memberservice.domain.dto.response.QMemberLoginResponseDTO
import com.albanote.memberservice.domain.entity.member.MemberType
import com.albanote.memberservice.domain.entity.member.QMember.member
import com.albanote.memberservice.domain.entity.member.QMemberFcmToken.memberFcmToken
import com.albanote.memberservice.repository.RepositorySupport
import org.springframework.stereotype.Repository

@Repository
class MemberRepository : RepositorySupport() {

    /** 소셜아이디로 멤버 존재 확인 **/
    fun findMemberBySocialId(socialId: String): String? {
        return select(member.socialId)
            .from(member)
            .where(member.socialId.eq(socialId))
            .fetchFirst()
    }

    fun findMemberPwdBySocialId(socialId: String): String? {
        return select(member.pwd)
            .from(member)
            .where(member.socialId.eq(socialId))
            .fetchFirst()
    }


    /** 소셜아이디로 멤버 정보 조회 **/
    fun findMemberInfoBySocialId(socialId: String?): MemberLoginResponseDTO? {
        return select(
            QMemberLoginResponseDTO(
                member.id,
                member.socialId,
                member.socialLoginType,
                member.osType,
                member.memberType
            )
        )
            .from(member)
            .where(member.socialId.eq(socialId))
            .fetchFirst()
    }

    /** 멤버 타입 선택 **/
    fun updateMemberType(memberId: Long, memberType: MemberType) {
        update(member)
            .where(member.id.eq(memberId))
            .set(member.memberType, memberType)
            .execute()
    }

    /** fcm token 변경 **/
    fun updateMemberFcmToken(memberId: Long, newFcmToken: String) {
        update(memberFcmToken)
            .where(memberFcmToken.member.id.eq(memberId))
            .set(memberFcmToken.fcmToken, newFcmToken)
            .execute()
    }
}
