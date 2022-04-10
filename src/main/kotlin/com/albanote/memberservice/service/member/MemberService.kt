package com.albanote.memberservice.service.member

import com.albanote.memberservice.domain.dto.response.MemberLoginResponseDTO
import com.albanote.memberservice.domain.entity.member.Member
import com.albanote.memberservice.domain.entity.member.MemberType
import com.albanote.memberservice.domain.entity.member.OsType
import com.albanote.memberservice.repository.member.MemberRepository
import com.albanote.memberservice.domain.entity.member.SocialLoginType
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val em: EntityManager
) : UserDetailsService {

    @Transactional
    fun memberCheckExistAndCreate(socialId: String, pwd: String?, socialLoginType: SocialLoginType, osType: OsType) {
        val isExist = memberRepository.findMemberBySocialId(socialId) == socialId

        if (!isExist) {
            val member = Member(socialId = socialId, socialLoginType = socialLoginType, osType = osType, pwd = pwd)
            em.persist(member)
        }
    }

    override fun loadUserByUsername(socialId: String?): UserDetails {
        socialId ?: throw UsernameNotFoundException("user name not found")
        val pwd = memberRepository.findMemberPwdBySocialId(socialId)
            ?: throw UsernameNotFoundException("user name not found")
        return User(socialId, pwd, true, true, true, true, listOf())
    }

    /** 소셜아이디로 멤버 정보 조회 **/
    fun getMemberInfo(socialId: String?): MemberLoginResponseDTO? {
        return memberRepository.findMemberInfoBySocialId(socialId)
    }

    @Transactional
    /** 멤버 타입 선택 **/
    fun putSelectMemberType(memberId: Long, memberType: MemberType) {
        memberRepository.updateMemberType(memberId, memberType)
    }

    @Transactional
    /** fcm token 변경 **/
    fun putMemberFcmToken(memberId: Long, fcmToken: String) {
        memberRepository.updateMemberFcmToken(memberId, fcmToken)
    }
}