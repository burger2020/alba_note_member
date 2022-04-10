package com.albanote.memberservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/common")
class CommonController {

    @GetMapping("/currentTime")
    fun getCurrentTime(): ResponseEntity<LocalDateTime> {
        return ResponseEntity.ok(LocalDateTime.now())
    }
}