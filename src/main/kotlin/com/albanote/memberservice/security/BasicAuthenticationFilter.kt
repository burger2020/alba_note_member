package com.albanote.memberservice.security

import com.albanote.memberservice.error.exception.AccessTokenNotValidException
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class BasicAuthenticationFilter(private val env: Environment) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        request as HttpServletRequest
        // /login 아닌 경로는 통과
        if (!request.requestURI.contains("/login")) {
            chain?.doFilter(request, response)
            return
        }

        // /member 경로는 access token 검사
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null)
            throw AccessTokenNotValidException("no basic token")

        val accessToken = request.getHeader("Authorization") ?: request.getHeader("authorization")
        val basic = accessToken.replace("Basic", "").replace(" ", "")

        if (isBasicTokenValid(basic)) chain?.doFilter(request, response)
        else throw AccessTokenNotValidException("invalid basic token")
    }

    /** jwt 검증 **/
    private fun isBasicTokenValid(basic: String): Boolean {
        try {
            val basicToken = Base64.getEncoder()
                .encodeToString("${env.getProperty("security.basic.server_id")}:${env.getProperty("security.basic.server_pwd")}".toByteArray())
            return basicToken == basic
        } catch (e: Exception) {
            throw AccessTokenNotValidException("invalid basic token")
        }
    }
}