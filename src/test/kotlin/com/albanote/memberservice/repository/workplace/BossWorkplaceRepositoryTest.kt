package com.albanote.memberservice.repository.workplace

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class BossWorkplaceRepositoryTest {

    @Autowired
    lateinit var bossWorkplaceRepository: BossWorkplaceRepository

    @Test
    fun test() {
        val memberId = 1L
        val result = bossWorkplaceRepository.findMyWorkplaceInfo(memberId, null)

        print(result!!.workplaceTitle)
    }
}