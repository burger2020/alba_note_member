package com.albanote.memberservice.service.workplace

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class BossWorkplaceServiceTest {
    @Autowired
    lateinit var bossWorkplaceService: BossWorkplaceService

    @Test
    fun fs() {
        val memberId = 10L
        val list = bossWorkplaceService.getWorkplaceList(memberId)
        list.forEach {

        }
    }
}