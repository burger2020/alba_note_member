package com.albanote.memberservice.service

import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.repository.WorkplaceRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkplaceService(
    private val workplaceRepository: WorkplaceRepository,
    private val s3service: S3Service
) {

    //TODO 전체 조회 테스트 ㄲㄲ
    /** 홈화면 대표 일터 정보 조회 **/
    fun getWorkplaceInfo(memberId: Long, workplaceId: Long?): WorkplaceInfoOfBossResponseDTO {
        val workplaceInfo = workplaceRepository.findMyWorkplaceInfo(memberId, workplaceId) ?: throw Exception("없는 일터")
        val pageable = PageRequest.of(0, 10)
        val completedTodos =
            workplaceRepository.findWorkplaceTodoRecordsByDate(workplaceInfo.workplaceId, true, pageable)
        val totalTodayTodoCount = workplaceRepository.findWorkplaceTodayTotalTodoCount(workplaceInfo.workplaceId)
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceInfo.workplaceId)
        val requestList = getRequestList(workplaceInfo.workplaceId, PageRequest.of(0, 10))

        workplaceInfo.completedTodos.addAll(completedTodos)
        workplaceInfo.totalTodoCount = totalTodayTodoCount
        workplaceInfo.currentEmployees.addAll(currentEmployees)
        workplaceInfo.workplaceRequest.addAll(requestList)

        workplaceInfo.completedTodos.forEach {
            it.completedMember?.imageUrl = s3service.convertCloudFrontUrl(it.completedMember?.imageUrl)
        }
        workplaceInfo.currentEmployees.forEach {
            it.currentEmployee.imageUrl = s3service.convertCloudFrontUrl(it.currentEmployee.imageUrl)
        }

        return workplaceInfo
    }

    /** 일터 목록 조회 **/
    fun getWorkplaceList(memberId: Long): List<WorkplaceListResponseDTO> {
        return workplaceRepository.findWorkplaceListByMember(memberId)
    }

    /** 요청 조회 **/
    fun getRequestList(workplaceId: Long, pageable: Pageable): List<WorkplaceRequestSimpleResponseDTO> {
        val requestList = workplaceRepository.findRequestListByWorkplace(workplaceId, pageable)
        requestList.forEach {
            it.requestMember.imageUrl = s3service.convertCloudFrontUrl(it.requestMember.imageUrl)
        }

        return requestList
    }

    /** 요청 상세 보기 **/
    fun getRequestDetail(requestId: Long): WorkplaceRequestDetailResponseDTO {
        val requestDetail = workplaceRepository.findRequestDetail(requestId) ?: throw Exception("존재하지 않는 요청")
        requestDetail.requestMember.imageUrl = s3service.convertCloudFrontUrl(requestDetail.requestMember.imageUrl)

        return requestDetail
    }

    /** 할 일 리스트 조회 **/
    fun getTodoRecordList(workplaceId: Long, pageable: Pageable): List<TodoRecordResponseDTO> {
        val todoRecords = workplaceRepository.findWorkplaceTodoRecordsByDate(workplaceId, false, pageable)
        todoRecords.forEach {
            it.completedMember?.imageUrl = s3service.convertCloudFrontUrl(it.completedMember?.imageUrl)
        }

        return todoRecords
    }

    /** 할 일 기록 상세 조회 **/
    fun getTodoRecordDetail(todoRecordId: Long?, todoId: Long): WorkplaceTodoRecordDetailResponseDTO {
        val todoRecordDetail = workplaceRepository.findWorkplaceTodoRecordDetail(todoRecordId, todoId)
        todoRecordDetail.todo?.chargeEmployee?.forEach {
            it.imageUrl = s3service.convertCloudFrontUrl(it.imageUrl)
        }

        return todoRecordDetail
    }

    /** 현재 근무 직원 + 그 외 직원 조회 **/
    fun getWorkRecordList(workplaceId: Long): MutableList<WorkRecordResponseDTO> {
        // 현재 근무중인 직원
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceId)

        // 그 외 직원
        val currentEmployeeIds = currentEmployees.map { it.currentEmployee.memberId }
        val employees = workplaceRepository.finEmployeesByWorkplace(workplaceId, currentEmployeeIds)

        currentEmployees.addAll(employees.map { WorkRecordResponseDTO(it, null, null) })

        currentEmployees.forEach {
            it.currentEmployee.imageUrl = s3service.convertCloudFrontUrl(it.currentEmployee.imageUrl)
        }

        return currentEmployees
    }
}
