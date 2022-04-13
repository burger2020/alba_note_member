package com.albanote.memberservice.controller

import com.albanote.memberservice.domain.dto.BodyWithMemberId
import com.albanote.memberservice.domain.dto.MemberIdRequestDTO
import com.albanote.memberservice.domain.entity.member.MemberType
import com.albanote.memberservice.service.member.MemberService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService
) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @GetMapping("/health_check")
    fun healthCheck(): String {
        val log = "It's-working-in-${applicationName}"
        logger.info(log)
        return log
    }

    /** 멤버 타입 선택 **/
    @PutMapping("/selectMemberType")
    fun putSelectMemberType(
        @RequestBody body: BodyWithMemberId<MemberType>
    ): ResponseEntity<Boolean> {
        memberService.putSelectMemberType(body.memberId, body.body)
        
        return ResponseEntity.ok(true)
    }

    /** fcm token 변경 **/
    @PutMapping("/fcmToken")
    fun putMemberFcmToken(
        @RequestBody body: BodyWithMemberId<String> // fcm token
    ): ResponseEntity<Boolean> {
        memberService.putMemberFcmToken(body.memberId, body.body)

        return ResponseEntity.ok(true)
    }

    /** 로그아웃 -> 토큰 정보 삭제 **/
    @PostMapping("/logout")
    fun postMemberLogout(body: MemberIdRequestDTO): ResponseEntity<Boolean> {
        memberService.postMemberLogout(body.memberId)

        return ResponseEntity.ok(true)
    }
}