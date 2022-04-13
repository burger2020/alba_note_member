package com.albanote.memberservice.security

import com.albanote.memberservice.domain.dto.request.member.MemberLoginRequestDTO
import com.albanote.memberservice.domain.dto.response.MemberTokenResponseDTO
import com.albanote.memberservice.domain.entity.member.SocialLoginType
import com.albanote.memberservice.redis.RedisMemberService
import com.albanote.memberservice.domain.entity.member.SocialLoginType.*
import com.albanote.memberservice.service.member.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.http.HttpMethod.*
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
            //todo naver, apple login + idToken to sub
            val pwd = env.getProperty("security.member.password")
            val sub = when (cred.socialLoginType) {
                APPLE, GOOGLE -> getSocialIdByGoogleIdToken(cred.socialId)
                KAKAO -> getSocialIdByKakaoToken(cred.socialId)
                NAVER -> getSocialIdByKakaoToken(cred.socialId)
                TEST -> "TEST_TOKEN:" + System.currentTimeMillis() % 1000
            }
            val token = UsernamePasswordAuthenticationToken(sub, pwd, listOf())

            memberService.memberCheckExistAndCreate(
                sub,
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
        val memberInfo = memberService.getMemberInfo(socialId)

        val now = System.currentTimeMillis()
        val secret = env.getProperty("token.secret")
        val accessToken = Jwts.builder()
            .setSubject(memberInfo?.id.toString())
            .setExpiration(Date(now + env.getProperty("token.expiration_time")!!.toLong()))
            .setIssuer(env.getProperty("token.issuer"))
            .setIssuedAt(Date())
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()

        val refreshSecret = env.getProperty("refresh_token.secret")
        val expiredDate = Date(now + env.getProperty("refresh_token.expiration_time")!!.toLong())
//        val expiredDate = Date(now + 20 * 1000) // 유효기간 30초
        val refreshToken = Jwts.builder()
            .setSubject(memberInfo?.id.toString())
            .setExpiration(expiredDate)
            .setIssuer(env.getProperty("refresh_token.issuer"))
            .setIssuedAt(Date())
            .signWith(SignatureAlgorithm.HS512, refreshSecret)
            .compact().also { token ->
                redisMemberService.setMemberRefreshToken(memberInfo!!.id!!, token, expiredDate)
            }

        memberInfo?.memberTokenInfo = MemberTokenResponseDTO(accessToken, refreshToken)

        response.status = HttpStatus.OK.value()
        response.characterEncoding = "utf-8"
        response.writer.write(ObjectMapper().writeValueAsString(memberInfo))
        response.flushBuffer()
    }

    private fun getSocialIdByGoogleIdToken(idToken: String): String {
        val url = "https://oauth2.googleapis.com/tokeninfo?id_token=$idToken"
        val json = postReceiveToCollector(url, null, GET, String::class.java).toString()
        val map = ObjectMapper().readValue(json, Map::class.java)
        return map["sub"].toString()
    }

    private fun getSocialIdByKakaoToken(idToken: String): String {
        val url = "https://kauth.kakao.com/oauth/tokeninfo?id_token=$idToken"
        val json = postReceiveToCollector(url, null, POST, String::class.java).toString()
        val map = ObjectMapper().readValue(json, Map::class.java)
        return map["sub"].toString()
    }

    private fun <T> postReceiveToCollector(url: String, obj: Any?, method: HttpMethod, responseType: Class<T>): T? {
        val duration = Duration.ofSeconds(5000)
        val restTemplate: RestTemplate = RestTemplateBuilder()
            .setConnectTimeout(duration)
            .setReadTimeout(duration)
            .build()

        val headers = HttpHeaders()
        if (obj != null) headers["Authorization"] = "Bearer $obj"
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(obj, headers)

        val responseEntity = restTemplate.exchange(url, method, request, responseType)

        return responseEntity.body
    }
}