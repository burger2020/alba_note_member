package com.albanote.memberservice.domain.dto.query.workplace

class EmployeeDTO(
    val id: Long,
    val memberId: Long,
    val workplaceId: Long,
    val employeeRankId :Long,
    val employeeTodoIds: List<Long>,

) {
}