package com.albanote.memberservice.controller.workplace

import com.albanote.memberservice.domain.dto.request.workplace.*
import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.service.workplace.BossWorkplaceService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("/workplace")
class BossWorkplaceController(
    private val workplaceService: BossWorkplaceService
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
    @GetMapping("/todoRecordList")
    fun getTodoRecordList(
        @RequestParam workplaceId: Long,
        @PageableDefault(page = 0, size = 20) pageable: Pageable
    ): ResponseEntity<List<TodoRecordResponseDTO>> {
        val recordList = workplaceService.getTodoRecordList(workplaceId, pageable)

        return ResponseEntity.ok(recordList)
    }

    /** 할 일 기록 상세 조회 **/
    @GetMapping("/todoRecordDetail")
    fun getTodoRecordDetail(
        @RequestParam todoRecordId: Long?,
        @RequestParam todoId: Long
    ): ResponseEntity<WorkplaceTodoRecordDetailResponseDTO> {
        val workplaceRecordDetail = workplaceService.getTodoRecordDetail(todoRecordId, todoId)

        return ResponseEntity.ok(workplaceRecordDetail)
    }

    /** 현재 근무 직원 + 그 외 직원 조회 **/
    @GetMapping("/workRecordList")
    fun getWorkRecord(
        @RequestParam workplaceId: Long
    ): ResponseEntity<MutableList<WorkRecordResponseDTO>> {
        val employees = workplaceService.getWorkRecordList(workplaceId)

        return ResponseEntity.ok(employees)
    }

    /** 일터 직원 간단한 정보 조회 **/
    @GetMapping("/simpleEmployeeList")
    fun getSimpleEmployeeList(
        @RequestParam workplaceId: Long
    ): ResponseEntity<List<EmployeeMemberSimpleResponseDTO>> {
        val employees = workplaceService.getSimpleEmployeeList(workplaceId)

        return ResponseEntity.ok(employees)
    }

    /** 일별 근무 기록 조회 **/
    @GetMapping("/workRecordByDate")
    fun getWorkRecordByDate(
        @RequestParam workplaceId: Long,
        @RequestParam date: LocalDate
    ): ResponseEntity<MutableList<WorkRecordResponseDTO>> {
        val workRecords = workplaceService.getWorkRecordByDate(workplaceId, date)

        return ResponseEntity.ok(workRecords)
    }

    @GetMapping("/wrokRecordDetail")
    fun getWorkRecordDetail(@RequestParam workRecordId: Long){
        workplaceService.getWorkRecordDetail(workRecordId)
    }

    /*********************   post   *********************/

    /** 일터 생성 **/
    @PostMapping("/createWokrplace")
    fun postCreateWorkplace(@RequestBody dto: CreateWorkplaceRequestDTO): ResponseEntity<Long?> {
        val workplaceId = workplaceService.postCreateWorkplace(dto)

        return ResponseEntity.ok(workplaceId)
    }

    /** 일터 사진 등록 **/
    @PostMapping("/createWorkplaceImage")
    fun postCreateWorkplaceImage(
        @RequestParam workplaceId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val imageUrl = workplaceService.postCreateWorkplaceImage(workplaceId, file)

        return ResponseEntity.ok(imageUrl)
    }

    /** 직책 생성 **/
    @PostMapping("/createEmployeeRank")
    fun postCreateEmployeeRank(@RequestBody dto: CreateEmployeeRankResponseDTO): ResponseEntity<Long> {
        val rankId = workplaceService.postCreateEmployeeRank(dto)

        return ResponseEntity.ok(rankId)
    }

    /** 할 일 생성 **/
    @PostMapping("/createTodo")
    fun postCreateTodo(@RequestBody dto: CreateWorkplaceTodoRequestDTO): ResponseEntity<Long?> {
        val todoId = workplaceService.postCreateTodo(dto)

        return ResponseEntity.ok(todoId)
    }

    /** 할 일 참조 사진 등록 **/
    @PostMapping("/createTodoReferenceImage")
    fun postCreateTodoReferenceImage(
        @RequestParam workplaceId: Long,
        @RequestParam("file") file: MultipartFile
    ) {
        workplaceService.postCreateTodoReferenceImage(workplaceId, file)
        //todo
    }

    /************************ put **************************/

    /** 대표 일터 변경 **/
    @PutMapping("/changeRepWorkplace")
    fun putChangeRepWorkplace(@RequestBody dto: ChangeRepWorkplaceRequestDTO): ResponseEntity<Boolean> {
        val response = workplaceService.putChangeRepWorkplace(dto)

        return ResponseEntity.ok(response)
    }

    /** 할 일 수정 **/
    @PutMapping("/modifyTodo")
    fun putModifyTodo(
        @RequestBody dto: ModifyWorkplaceTodoRequestDTO,
    ): ResponseEntity<Long> {
        val todoId = workplaceService.putModifyTodo(dto)

        return ResponseEntity.ok(todoId)
    }

    /** 직책 정보 수정 **/
    @PutMapping("/modifyEmployeeRank")
    fun putModifyEmployeeRank(@RequestBody dto: ModifyEmployeeRankResponseDTO): ResponseEntity<Long> {
        val rankId = workplaceService.putModifyEmployeeRank(dto)

        return ResponseEntity.ok(rankId)
    }

    /************************ delete **************************/

    /** 일터 비활성화 **/
    @DeleteMapping("/deleteWorkplace")
    fun deleteDeleteWorkplace(@RequestBody dto: DeleteRepWorkplaceRequestDTO): ResponseEntity<Boolean> {
        val result = workplaceService.deleteDeleteWorkplace(dto.memberId, dto.workplaceId)

        return ResponseEntity.ok(result)
    }

    /** 할 일 삭제 **/
    @DeleteMapping("/deleteTodo")
    fun deleteDeleteTodo(@RequestParam todoId: Long): ResponseEntity<Boolean> {
        val result = workplaceService.deleteDeleteTodo(todoId)

        return ResponseEntity.ok(result)
    }

    /** 직책 삭제 **/
    @DeleteMapping("/deleteEmployeeRank")
    fun deleteDeleteEmployeeRank(@RequestParam rankId: Long): ResponseEntity<Boolean> {
        val result = workplaceService.deleteDeleteEmployeeRank(rankId)

        return ResponseEntity.ok(result)
    }
}