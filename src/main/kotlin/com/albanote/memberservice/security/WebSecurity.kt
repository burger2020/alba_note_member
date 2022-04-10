package com.albanote.memberservice.security

import com.albanote.memberservice.redis.RedisMemberService
import com.albanote.memberservice.service.member.MemberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurity(
    val userService: MemberService,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    val env: Environment,
    val redisMemberService: RedisMemberService
) : WebSecurityConfigurerAdapter() {

//    @Value("\${security.access.ipAddress}")
//    lateinit var accessIpAddress: List<String>

    // 권한
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()

        http.authorizeRequests()
            .antMatchers("/**")
            .permitAll()
            .and()
            .addFilterBefore(BasicAuthenticationFilter(env), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JwtAuthenticationFilter(env), UsernamePasswordAuthenticationFilter::class.java)
            .addFilter(getAuthenticationFilter())

//        http.authorizeRequests()
//            .antMatchers("/**")
//            .access(
//                "hasIpAddress('192.168.0.13') or " +
//                        "hasIpAddress('192.168.0.11') or " +
//                        "hasIpAddress('172.31.46.231') or " +
//                        "hasIpAddress('13.124.178.242')" +
//                        if (!accessIpAddress.isNullOrEmpty()) {
//                            var ip = ""
//                            accessIpAddress.forEach { ip += " or hasIpAddress('$it/16')" }
//                            ip
//                        } else ""
//            )


        http.headers().frameOptions().disable()
    }

    // 인증
    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder)
        super.configure(auth)
    }

    private fun getAuthenticationFilter(): AuthenticationFilter {
        return AuthenticationFilter(
            userService,
            env,
            bCryptPasswordEncoder,
            authenticationManager(),
            redisMemberService
        ).apply {
            setAuthenticationManager(authenticationManager())
        }
    }
}