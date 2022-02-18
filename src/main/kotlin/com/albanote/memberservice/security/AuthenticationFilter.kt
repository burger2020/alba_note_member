//package com.albanote.memberservice.security
//
//import com.albanote.memberservice.security.SocialLoginType.*
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.sanho.hitup.memberservice.domain.dto.app.MemberLoginResultDTO
//import com.sanho.hitup.memberservice.domain.dto.app.request.MemberLoginRequestDTO
//import com.sanho.hitup.memberservice.domain.dto.app.response.MemberTokenResponseDTO
//import com.sanho.hitup.memberservice.redis.member.RedisMemberService
//import com.sanho.hitup.memberservice.service.MemberService
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.SignatureAlgorithm
//import org.springframework.boot.web.client.RestTemplateBuilder
//import org.springframework.core.env.Environment
//import org.springframework.http.*
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
//import org.springframework.util.Base64Utils
//import org.springframework.web.client.RestTemplate
//import java.io.IOException
//import java.time.Duration
//import java.util.*
//import javax.servlet.FilterChain
//import javax.servlet.ServletException
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//class AuthenticationFilter(
//    private val memberService: MemberService,
//    private val env: Environment,
//    private val passwordEncoder: BCryptPasswordEncoder,
//    authenticationManager: AuthenticationManager,
//    private val redisMemberService: RedisMemberService
//) : UsernamePasswordAuthenticationFilter(authenticationManager) {
//
//    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
//        try {
//            // 매핑에러나면 dto 생성자 있는지 확인 (gradle noArg)
//            val cred = ObjectMapper().readValue(request.inputStream, MemberLoginRequestDTO::class.java)
//
//            val password = env.getProperty("member.password")
//
//            val idToken = when (cred.socialType) {
//                APPLE, GOOGLE -> cred.socialType.name + getSocialIdByIdToken(cred.socialToken)
//                KAKAO -> cred.socialType.name + getSocialIdByKakaoToken(cred.socialToken)
//                TEST -> cred.socialType.name + cred.socialToken + System.currentTimeMillis() % 1000
//            }
//
//            val token = UsernamePasswordAuthenticationToken(idToken, password, listOf())
//
//            memberService.memberCheckExistAndCreate(idToken, passwordEncoder.encode(password), cred.socialType, cred.osType)
//
//            return authenticationManager.authenticate(token)
//        } catch (e: IOException) {
//            throw RuntimeException()
//        }
//    }
//
//    @Throws(IOException::class, ServletException::class)
//    override fun successfulAuthentication(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        chain: FilterChain,
//        authResult: Authentication
//    ) {
//        val socialId = (authResult.principal as User).username
//        val now = System.currentTimeMillis()
//        val secret = env.getProperty("token.secret")
//        val accessToken = Jwts.builder()
//            .setSubject(socialId)
////            .setExpiration(Date(now + 5000L)) // 토큰 유효기간 10초 테스트
//            .setExpiration(Date(now + env.getProperty("token.expiration_time")!!.toLong()))
//            .signWith(SignatureAlgorithm.HS512, secret)
//            .compact()
//
//        val refreshSecret = env.getProperty("refresh_token.secret")
//        val expiredDate = Date(now + env.getProperty("refresh_token.expiration_time")!!.toLong())
////        val expiredDate = Date(now + 20 * 1000) // 유효기간 30초
//        val refreshToken = Jwts.builder()
//            .setSubject(socialId)
//            .setExpiration(expiredDate)
//            .signWith(SignatureAlgorithm.HS512, refreshSecret)
//            .compact()
//
//        redisMemberService.setMemberRefreshToken(socialId, refreshToken, expiredDate)
//
//        val memberInfo = memberService.getMemberInfo(socialId)
//        memberInfo.memberTokenInfo = MemberTokenResponseDTO(accessToken, refreshToken)
//
//        response.status = HttpStatus.OK.value()
//        response.characterEncoding = "utf-8"
//        response.writer.write(ObjectMapper().writeValueAsString(MemberLoginResultDTO(memberInfo)))
//        response.flushBuffer()
//    }
//
//    private fun getSocialIdByIdToken(idToken: String): String {
//        val chunks = idToken.split(".")
//        val payload = String(Base64Utils.decodeFromUrlSafeString(chunks[1]))
//        val map = ObjectMapper().readValue(payload, Map::class.java)
//        return map["sub"] as String
//    }
//
//    private fun getSocialIdByKakaoToken(idToken: String): String {
//        val url = "https://kapi.kakao.com/v2/user/me"
//        val json = postReceiveToCollector(url, idToken, String::class.java).toString()
//        val map = ObjectMapper().readValue(json, Map::class.java)
//        return map["id"].toString()
//    }
//
//    private fun <T> postReceiveToCollector(url: String, obj: Any?, responseType: Class<T>): T? {
//        val duration = Duration.ofSeconds(5000)
//        val restTemplate: RestTemplate = RestTemplateBuilder()
//            .setConnectTimeout(duration)
//            .setReadTimeout(duration)
//            .build()
//
//        val headers = HttpHeaders()
//        if (obj != null) headers["Authorization"] = "Bearer $obj"
//        headers.contentType = MediaType.APPLICATION_JSON
//
//        val request = HttpEntity(obj, headers)
//
//        val responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, responseType)
//
//        return responseEntity.body
//    }
//
//}