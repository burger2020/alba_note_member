package com.albanote.memberservice.domain.dto.response.workplace

import com.albanote.memberservice.domain.entity.workplace.work.WorkplaceRequestType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


// 최근 요청
class WorkplaceRequestDetailResponseDTO @QueryProjection constructor(
    var requestId: Long,
    var createdDate: LocalDateTime,
    var requestType: WorkplaceRequestType,
    var requestContent: String,
    var isComplete: Boolean?,
    var memo: String?,
    var requestMember: EmployeeMemberSimpleResponseDTO,
    var requestMemo: String?,
    // 근무 생성 요청 시 근무 날짜
    var requestWorkDate: LocalDate,
    // 요청 출퇴근 시간
    var requestOfficeGoingTime: LocalTime,
    var requestQuittingTime: LocalTime,


    ) {
    // 기존 출퇴근 시간 -
    var existingOfficeGoingTime: LocalTime? = null
    var existingQuittingTime: LocalTime? = null
    //todo 이거는 근무 기록 (workRecord) 에서 가져오면 됨
}