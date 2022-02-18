package com.albanote.memberservice.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class MemberController {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @GetMapping("/health_check")
    fun healthCheck(): String {
        val log = "It's-working-in-${applicationName}"
        logger.info(log)
        return log
    }
}