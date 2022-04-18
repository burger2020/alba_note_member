package com.albanote.memberservice.service.workplace

import com.albanote.memberservice.repository.workplace.WorkplaceScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkplaceScheduleService(
    val workplaceScheduleRepository: WorkplaceScheduleRepository
) {

    /** 결근 기록 생성 **/
    @Transactional
    fun setAbsentWorkRecord() {
        workplaceScheduleRepository.setAbsentWorkRecord()
    }

    /** 주휴 휴무 기록 생성 **/
    @Transactional
    fun setPaidHolidayWorkRecord(){
        workplaceScheduleRepository.setPaidHolidayWorkRecord()
    }
}