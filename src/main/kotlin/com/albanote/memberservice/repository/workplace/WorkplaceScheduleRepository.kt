package com.albanote.memberservice.repository.workplace

import com.albanote.memberservice.domain.dto.QQueryDslPairDTO
import com.albanote.memberservice.domain.dto.QueryDslPairDTO
import com.albanote.memberservice.domain.entity.workplace.QCommuteTimeByDayOfWeek.commuteTimeByDayOfWeek
import com.albanote.memberservice.domain.entity.workplace.QEmployeeMember.employeeMember
import com.albanote.memberservice.domain.entity.workplace.QEmployeeRank.employeeRank
import com.albanote.memberservice.domain.entity.workplace.work.QWorkRecord.workRecord
import com.albanote.memberservice.domain.entity.workplace.work.WorkType
import com.albanote.memberservice.repository.RepositorySupport
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.LocalTime

@Repository
class WorkplaceScheduleRepository(
    private val jdbcTemplate: JdbcTemplate
) : RepositorySupport() {

    fun setAbsentWorkRecord() {
        val absentEmployeeRankIds = select(employeeRank.id)
            .from(employeeRank)
            .where(
                employeeRank.quittingTime.gt(LocalTime.now().minusMinutes(5)),
                employeeRank.quittingTime.lt(LocalTime.now().plusMinutes(5))
            ).fetch()
            .toMutableList()

        absentEmployeeRankIds.addAll(
            select(commuteTimeByDayOfWeek.employeeRank.id)
                .from(commuteTimeByDayOfWeek)
                .where(
                    commuteTimeByDayOfWeek.quittingTime.gt(LocalTime.now().minusMinutes(5)),
                    commuteTimeByDayOfWeek.quittingTime.lt(LocalTime.now().plusMinutes(5))
                ).fetch()
        )

        val absentEmployeeMemberIds = select(QQueryDslPairDTO(employeeMember.id, employeeMember.workplace.id))
            .from(employeeMember)
            .where(employeeMember.employeeRank.id.`in`(absentEmployeeRankIds))
            .fetch()

        val excludeEmployeeIds = select(workRecord.employeeMember.id)
            .from(workRecord)
            .where(
                workRecord.workType.ne(WorkType.ABSENT),
                workRecord.workDate.eq(LocalDate.now())
            ).fetch()

        val absentWorkRecords = absentEmployeeMemberIds.filter { !excludeEmployeeIds.contains(it.first as Long) }
        workRecordBatchInsert(absentWorkRecords, WorkType.ABSENT)
    }

    /** 주휴 휴무 기록 생성 **/
    fun setPaidHolidayWorkRecord() {
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        val paidHolidayEmployeeRankIds = select(employeeRank.id)
            .from(employeeRank)
            .where(employeeRank.paidHoliday.substring(dayOfWeek - 1, dayOfWeek).eq("1"))
            .fetch()
        val paidHolidayEmployeeMemberIds = select(QQueryDslPairDTO(employeeMember.id, employeeMember.workplace.id))
            .from(employeeMember)
            .where(employeeMember.employeeRank.id.`in`(paidHolidayEmployeeRankIds))
            .fetch()

        val holidayEmployeeRankIds = select(employeeRank.id)
            .from(employeeRank)
            .where(employeeRank.workingDay.substring(dayOfWeek - 1, dayOfWeek).eq("_"))
            .fetch()
        val holidayEmployeeMemberIds = select(QQueryDslPairDTO(employeeMember.id, employeeMember.workplace.id))
            .from(employeeMember)
            .where(employeeMember.employeeRank.id.`in`(holidayEmployeeRankIds))
            .fetch()

        workRecordBatchInsert(paidHolidayEmployeeMemberIds, WorkType.PAID_HOLIDAY)
        workRecordBatchInsert(holidayEmployeeMemberIds, WorkType.HOLIDAY)
    }

    fun workRecordBatchInsert(
        employeeIdAndWorkplaceIdPair: List<QueryDslPairDTO>,
        workType: WorkType,
        pay: Int = 0
    ) {
        val sql =
            "INSERT INTO WORK_RECORD (employee_member_id, workplace_id, work_type, work_date, pay) VALUES (?,?,?,?,?)"
        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun getBatchSize(): Int = employeeIdAndWorkplaceIdPair.size
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setLong(1, employeeIdAndWorkplaceIdPair[i].first as Long)
                ps.setLong(2, employeeIdAndWorkplaceIdPair[i].second as Long)
                ps.setString(3, workType.name)
                ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()))
                ps.setInt(5, pay)
            }
        })
    }
}