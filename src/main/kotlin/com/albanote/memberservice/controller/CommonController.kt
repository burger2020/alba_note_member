package com.albanote.memberservice.controller

import com.albanote.memberservice.domain.dto.MemberIdRequestDTO
import com.albanote.memberservice.domain.dto.response.MemberTokenResponseDTO
import com.albanote.memberservice.error.exception.RefreshTokenNotValidException
import com.albanote.memberservice.redis.RedisMemberService
import io.jsonwebtoken.*
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/common")
class CommonController(
    private val redisMemberService: RedisMemberService,
    private val env: Environment
) {

    @GetMapping("/currentTime")
    fun getCurrentTime(): ResponseEntity<LocalDateTime> {
        return ResponseEntity.ok(LocalDateTime.now())
    }

    //    @Operation(summary = "accessToken 재발급", parameters = [Parameter()])
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "401",
//                description = "refreshToken Null 이거나 유효하지 않움 - code: 601",
//                content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorDTO::class))]
//            )
//        ]
//    )
    @PostMapping("/checkValidAccessToken")
    fun postCheckValidAccessToken(): ResponseEntity<Boolean> {
        return ResponseEntity.ok(true)
    }

    @PostMapping("/refresh")
    fun postRefreshToken(
        @RequestBody dto: MemberIdRequestDTO,
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<MemberTokenResponseDTO> {
        val refreshToken = headers.toSingleValueMap()["authorization"]
            ?.replace("Bearer", "")?.replace(" ", "")
            ?: headers.toSingleValueMap()["Authorization"]
                ?.replace("Bearer", "")?.replace(" ", "")
            ?: throw RefreshTokenNotValidException("Null JWT Refresh token.")
        val memberId = dto.id
        if (redisMemberService.isValidMemberRefreshToken(memberId, refreshToken)) {
            val now = System.currentTimeMillis()
            val secret = env.getProperty("token.secret")
            val accessToken = Jwts.builder()
                .setSubject(memberId)
                .setExpiration(Date(now + env.getProperty("token.expiration_time")!!.toLong()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()

            // refresh token 유효기간 1주 이내일 시 새로운 refresh token 발급
            val newRefreshToken = if (!isRefreshTokenValidExpiration(refreshToken)) {
                val refreshSecret = env.getProperty("refresh_token.secret")
                val expiredDate = Date(now + env.getProperty("refresh_token.expiration_time")!!.toLong())
                Jwts.builder()
                    .setSubject(memberId)
                    .setExpiration(expiredDate)
                    .signWith(SignatureAlgorithm.HS512, refreshSecret)
                    .compact().also { token ->
                        redisMemberService.setMemberRefreshToken(memberId, token, expiredDate)
                    }
            } else null
            val tokenDTO = MemberTokenResponseDTO(accessToken, newRefreshToken)
            return ResponseEntity.ok(tokenDTO)
        } else throw RefreshTokenNotValidException("Invalid JWT refresh token.")
    }

    /**
     * refresh token 유효기간 7일 이상 남았는지 확인
     *
     * @author burger
     * */
    private fun isRefreshTokenValidExpiration(jwt: String): Boolean {
        return try {
            val secret = env.getProperty("refresh_token.secret")
            val jwts = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
            val date = Date(System.currentTimeMillis() + 60 * 60 * 24 * 7 * 1000) // 7일
            jwts.body.expiration > date
        } catch (e: SignatureException) {
            throw RefreshTokenNotValidException("Invalid JWT refresh token signature.")
        } catch (e: MalformedJwtException) {
            throw RefreshTokenNotValidException("Invalid JWT refresh token.")
        } catch (e: ExpiredJwtException) {
            throw RefreshTokenNotValidException("Expired JWT refresh token.")
        } catch (e: UnsupportedJwtException) {
            throw RefreshTokenNotValidException("Unsupported JWT refresh token.")
        } catch (e: IllegalArgumentException) {
            throw RefreshTokenNotValidException("JWT refresh token compact of handler are invalid.")
        } catch (e: Exception) {
            throw RefreshTokenNotValidException("Invalid JWT refresh token.")
        }
    }
}