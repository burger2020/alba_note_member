package com.albanote.memberservice.service

import com.albanote.memberservice.repository.WorkplaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkplaceService(
    private val workplaceRepository: WorkplaceRepository,
//    private val s3service:
) {

    //TODO 전체 조회 테스트 ㄲㄲ
    /** 홈화면 대표 일터 정보 조회 **/
    fun getMyRepWorkplaceInfo(memberId: Long, workplaceId: Long) {
        val workplaceInfo = workplaceRepository.findMyWorkplaceInfo(memberId, workplaceId) ?: throw Exception("없는 일터")

        val completedTodos = workplaceRepository.findWorkplaceTodayCompletedTodo(workplaceId)
        val totalTodayTodoCount = workplaceRepository.findWorkplaceTodayTotalTodoCount(workplaceId)
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceId)

        //todo s3service 로 이미지 url 변경
        workplaceInfo.completedTodos.addAll(completedTodos)
        workplaceInfo.totalTodoCount = totalTodayTodoCount
        workplaceInfo.currentEmployees.addAll(currentEmployees)
    }

}
