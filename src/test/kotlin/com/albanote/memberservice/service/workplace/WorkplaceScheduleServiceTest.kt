package com.albanote.memberservice.service.workplace

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
internal class WorkplaceScheduleServiceTest{
    @Autowired
    lateinit var workplaceScheduleService: WorkplaceScheduleService

    @Test
    @Commit
    fun 결근_기록_생성(){
        workplaceScheduleService.setAbsentWorkRecord()
    }
}