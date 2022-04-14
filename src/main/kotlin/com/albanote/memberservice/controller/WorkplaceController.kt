package com.albanote.memberservice.controller

import com.albanote.memberservice.domain.dto.response.workplace.WorkplaceInfoOfBossResponseDTO
import com.albanote.memberservice.domain.dto.response.workplace.WorkplaceListResponseDTO
import com.albanote.memberservice.domain.dto.response.workplace.WorkplaceRequestDetailResponseDTO
import com.albanote.memberservice.domain.dto.response.workplace.WorkplaceRequestSimpleResponseDTO
import com.albanote.memberservice.service.WorkplaceService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workplace")
class WorkplaceController(
    private val workplaceService: WorkplaceService
) {

    /** 대표 일터 조회
     * @param workplaceId - null 이면 대표 일터
     * **/
    @GetMapping("/workplaceInfoOfBoss")
    fun getWorkplaceInfoOfBoss(
        @RequestParam memberId: Long,
        @RequestParam workplaceId: Long?
    ): ResponseEntity<WorkplaceInfoOfBossResponseDTO> {
        val result = workplaceService.getWorkplaceInfo(memberId, workplaceId)
        return ResponseEntity.ok(result)
    }

    /** 일터 목록 조회 **/
    @GetMapping("/workplaceList")
    fun getWorkplaceList(
        @RequestParam memberId: Long
    ): ResponseEntity<List<WorkplaceListResponseDTO>> {
        val result = workplaceService.getWorkplaceList(memberId)
        return ResponseEntity.ok(result)
    }

    /** 일터 요청 조회 **/
    @GetMapping("/requestList")
    fun getRequestList(
        @RequestParam workplaceId: Long,
        @PageableDefault(page = 0, size = 20) pageable: Pageable
    ): ResponseEntity<List<WorkplaceRequestSimpleResponseDTO>> {
        val requestList = workplaceService.getRequestList(workplaceId, pageable)

        return ResponseEntity.ok(requestList)
    }

    /** 일터 요청 자세히 보기 **/
    @GetMapping("/requestDetail")
    fun getRequestDetail(
        @RequestParam requestId: Long
    ): ResponseEntity<WorkplaceRequestDetailResponseDTO> {
        val requestDetail = workplaceService.getRequestDetail(requestId)

        return ResponseEntity.ok(requestDetail)
    }

    /** 할 일 리스트 조회 **/
    @GetMapping("/todoList")
    fun getTotoList(
        @RequestParam workplaceId: Long,
        @PageableDefault(page = 0, size = 20) pageable: Pageable
    ){
        workplaceService.getTodoList(workplaceId, pageable)

    }
}