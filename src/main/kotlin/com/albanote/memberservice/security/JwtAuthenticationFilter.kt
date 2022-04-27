package com.albanote.memberservice.security

import com.albanote.memberservice.domain.dto.ErrorDTO
import com.albanote.memberservice.error.exception.AccessTokenNotValidException
import com.albanote.memberservice.error.exception.BaseException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtAuthenticationFilter(private val env: Environment) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        request as HttpServletRequest
        if (request.requestURI.contains("/health_check") ||
            request.requestURI.contains("/admin") ||
            request.requestURI.contains("/login") ||
            request.requestURI.contains("/websocket") ||
            request.requestURI.contains("/error") ||
            request.requestURI.contains("/refreshToken")||
            request.requestURI.contains("/favicon.ico")
        ) {
            try {
                chain?.doFilter(request, response)
            } catch (e: NotExistMemberIdException) { // 존재하지 않는 회원
                onException(e, response)
            }
            return
        }

        // /member 경로는 access token 검사
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null)
            onException(AccessTokenNotValidException("no access token"), response)

        val accessToken = request.getHeader("Authorization") ?: request.getHeader("authorization")
        val jwt = accessToken.replace("Bearer", "").replace(" ", "")

        if (isJwtValid(jwt)) chain?.doFilter(request, response)
        else onException(AccessTokenNotValidException("invalid jwt token"), response)
    }

    private fun onException(e: BaseException, response: ServletResponse?) {
        response as HttpServletResponse
        response.status =
            if (e is AccessTokenNotValidException) HttpStatus.UNAUTHORIZED.value() else HttpStatus.FORBIDDEN.value()
        response.contentType = "application/json; charset=UTF-8"
        val errorDTO = ErrorDTO(message = e.message, code = e.code)
        val mapper = ObjectMapper()

        response.writer.write(mapper.writeValueAsString(errorDTO))
    }

    /** jwt 검증 **/
    private fun isJwtValid(jwt: String): Boolean {
        return try {
            val secret = env.getProperty("token.secret")
            val jwts = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
            when {
                jwts.body.subject.isNullOrEmpty() -> false
                jwts.body.expiration < Date(System.currentTimeMillis()) -> false
                else -> true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun onError(exchange: ServerWebExchange, s: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus

        logger.error(s)
        return response.setComplete()
    }
}