package com.albanote.memberservice.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class WorkplaceRepositoryTest {
    @Autowired
    lateinit var workplaceRepository: WorkplaceRepository

    @Test
    fun sadf() {
        val workplaceId = 0L
        val count = workplaceRepository.findWorkplaceTodayTotalTodoCount(workplaceId)

//        val substring = workplaceRepository.()
//        println("count =========== $count + $substring")
    }
}