package com.albanote.memberservice.security

import com.albanote.memberservice.domain.dto.request.member.MemberLoginRequestDTO
import com.albanote.memberservice.domain.dto.response.MemberTokenResponseDTO
import com.albanote.memberservice.redis.RedisMemberService
import com.albanote.memberservice.domain.entity.member.SocialLoginType.*
import com.albanote.memberservice.service.member.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.util.Base64Utils
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.time.Duration
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** 소셜 로그인 테스트 ㄲ **/
class AuthenticationFilter(
    private val memberService: MemberService,
    private val env: Environment,
    private val passwordEncoder: BCryptPasswordEncoder,
    authenticationManager: AuthenticationManager,
    private val redisMemberService: RedisMemberService
) : UsernamePasswordAuthenticationFilter(authenticationManager) {

    // '/login'
    //todo 앱 -> 소셜아이디 넘어오는거 까지 됨 - 넘어온 아이디 인증? or 그냥 회원가입,로그인?
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        try {
            val cred = ObjectMapper().readValue(request.inputStream, MemberLoginRequestDTO::class.java)
            //todo 재검증이 필요한가?
            val pwd = env.getProperty("security.member.password")
            val token = UsernamePasswordAuthenticationToken(cred.socialId, pwd, listOf())

            memberService.memberCheckExistAndCreate(
                cred.socialId,
                passwordEncoder.encode(pwd),
                cred.socialLoginType,
                cred.osType
            )
            return authenticationManager.authenticate(token)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val socialId = (authResult.principal as User).username
        val now = System.currentTimeMillis()
        val secret = env.getProperty("token.secret")
        val accessToken = Jwts.builder()
            .setSubject(socialId)
//            .setExpiration(Date(now + 5000L)) // 토큰 유효기간 10초 테스트
            .setExpiration(Date(now + env.getProperty("token.expiration_time")!!.toLong()))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()

        val refreshSecret = env.getProperty("refresh_token.secret")
        val expiredDate = Date(now + env.getProperty("refresh_token.expiration_time")!!.toLong())
//        val expiredDate = Date(now + 20 * 1000) // 유효기간 30초
        val refreshToken = Jwts.builder()
            .setSubject(socialId)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS512, refreshSecret)
            .compact()

        redisMemberService.setMemberRefreshToken(socialId, refreshToken, expiredDate)

        val memberInfo = memberService.getMemberInfo(socialId)
        memberInfo?.memberTokenInfo = MemberTokenResponseDTO(accessToken, refreshToken)

        response.status = HttpStatus.OK.value()
        response.characterEncoding = "utf-8"
        response.writer.write(ObjectMapper().writeValueAsString(memberInfo))
        response.flushBuffer()
    }
}