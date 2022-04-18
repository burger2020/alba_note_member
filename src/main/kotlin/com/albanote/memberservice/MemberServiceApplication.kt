package com.albanote.memberservice

import com.albanote.memberservice.logtrace.LogTrace
import com.albanote.memberservice.logtrace.ThreadLocalLogTrace
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.EntityManager

@EnableScheduling
@SpringBootApplication
//@SpringBootApplication(exclude= [DataSourceAutoConfiguration::class]) // db 연결 없이 실행 시
@EnableJpaAuditing
class MemberServiceApplication{

	@Bean
	fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun jpaQueryFactory(em: EntityManager) = JPAQueryFactory(em)

	@Bean
	fun logTrace(): LogTrace = ThreadLocalLogTrace()
}

fun main(args: Array<String>) {
	runApplication<MemberServiceApplication>(*args)
}
