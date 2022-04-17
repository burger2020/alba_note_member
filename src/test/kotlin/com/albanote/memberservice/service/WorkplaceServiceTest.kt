package com.albanote.memberservice.service

import com.albanote.memberservice.domain.dto.request.workplace.CreateWorkplaceTodoRequestDTO
import com.albanote.memberservice.domain.entity.workplace.work.TodoCycleType
import com.albanote.memberservice.service.workplace.BossWorkplaceService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import java.time.LocalTime

@SpringBootTest
internal class WorkplaceServiceTest {

    @Autowired
    lateinit var workplaceService: BossWorkplaceService

    @Test
    @Commit
    fun 할일생성() {
        val dto = CreateWorkplaceTodoRequestDTO(
            workplaceId = 0,
            todoTitle = "타이틀",
            todoDescription = "설먕~~~",
            chargeEmployeeIds = listOf(0),
            isAuthenticationImageRequire = true,
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            todoCycleType = TodoCycleType.DAILY,
            todoDays = null,
        )
        workplaceService.postCreateTodo(dto)
    }
}