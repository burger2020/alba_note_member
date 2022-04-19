package com.albanote.memberservice.domain.dto.response.workplace

import com.querydsl.core.annotations.QueryProjection

/**
 * 사장의 대표 일터 정보 조회 DTO
 * */
class WorkplaceInfoOfBossResponseDTO @QueryProjection constructor(
    val workplaceId: Long,
    val workplaceTitle: String,
    var workplaceImageUrl: String?
) {
    var workplaceRequest: MutableList<WorkplaceRequestSimpleResponseDTO> = mutableListOf()
    var currentEmployees: MutableList<WorkRecordResponseDTO> = mutableListOf()
    var completedTodos: MutableList<TodoRecordResponseDTO> = mutableListOf()
    var totalTodoCount: Int = 0
    var totalEmployeeCount: Int = 0
}