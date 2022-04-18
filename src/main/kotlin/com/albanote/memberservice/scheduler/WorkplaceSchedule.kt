package com.albanote.memberservice.scheduler

import com.albanote.memberservice.service.workplace.WorkplaceScheduleService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WorkplaceSchedule(
    private val workplaceScheduleService: WorkplaceScheduleService
) {

    /** 결근 기록 생성 **/
    @Scheduled(cron = "0 0/10 * * * ?")
    fun scheduleMakeAbsentRecord() {
        workplaceScheduleService.setAbsentWorkRecord()
    }

    /** 주휴 휴무 기록 생성 **/
    @Scheduled(cron = "0 0 0/1 * * ?")
    fun scheduleMakeHolidayRecord() {
        workplaceScheduleService.setPaidHolidayWorkRecord()
    }
}