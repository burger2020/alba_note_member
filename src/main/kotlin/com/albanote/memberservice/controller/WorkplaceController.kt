package com.albanote.memberservice.controller

import com.albanote.memberservice.service.WorkplaceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workplace")
class WorkplaceController(
    private val workplaceService: WorkplaceService
) {

    /** 대표 일터 조회**/
    @GetMapping("/repWorkplaceInfoOfBoss")
    fun getRepWorkplaceInfoOfBoss(
        @RequestParam memberId: Long,
        @RequestParam workplaceId: Long
    ) {
        workplaceService.getMyRepWorkplaceInfo(memberId, workplaceId)
    }
}