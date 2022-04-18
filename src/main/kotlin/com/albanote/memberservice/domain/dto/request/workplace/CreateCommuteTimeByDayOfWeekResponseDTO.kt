package com.albanote.memberservice.domain.dto.request.workplace

import com.albanote.memberservice.domain.entity.workplace.CommuteTimeByDayOfWeek
import com.albanote.memberservice.domain.entity.workplace.EmployeeRank
import java.time.LocalTime

/**
 * 요일별 출퇴근 시간 다르게 생성 요청 DTO
 * */
class CreateCommuteTimeByDayOfWeekResponseDTO(
    // 해당 요일 월,화,수 -> 111____
    val dayOfWeek: String,
    val officeGoingTime: LocalTime,
    val quittingTime: LocalTime
) {
    fun convertToEntity(rank: EmployeeRank): CommuteTimeByDayOfWeek {
        return CommuteTimeByDayOfWeek(
            employeeRank = rank,
            dayOfWeek = dayOfWeek,
            officeGoingTime = officeGoingTime,
            quittingTime = quittingTime
        )
    }
}