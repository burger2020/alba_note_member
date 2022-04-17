package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import com.albanote.memberservice.domain.entity.workplace.work.*
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_id"))
class Workplace(
    id: Long? = null,

    // 비활성화 시간
    val deprecatedDate: LocalDateTime? = null,
    // 일터 이름
    @Column(columnDefinition = "TEXT", nullable = false)
    val title: String? = null,

    // 직원 수
    val maxEmployeeNum: Int? = null,

    // 일터 주소
    @Column(columnDefinition = "TEXT", nullable = false)
    val address: String? = null,
    @Column(columnDefinition = "TEXT", nullable = false)
    val detailAddress: String? = null,

    // 출퇴근 기록 방식 //todo 일단 보류, 둘 다 넣을지 하나만 넣을지 정해
//    @Enumerated(EnumType.STRING)
//    @Column(columnDefinition = "TEXT", nullable = true)
//    var commuteRecordType: CommuteRecordType? = null,

    // 출퇴근용 와이파이 맥주소
    @Column(columnDefinition = "TEXT", nullable = true)
    var commuteRecordWifiMacAddress: String? = null,

    // 출퇴근용 좌표 및 범위
    var commuteRecordCoordinate: Coordinate? = null,
    var commuteRecordRadius: Int? = null,

    @Embedded
    var businessCertification: BusinessCertificationInfo? = null,

    // 직급
    @OneToMany(mappedBy = "workplace")
    val employeeRanks: MutableList<EmployeeRank> = mutableListOf(),

    // 직원
    @OneToMany(mappedBy = "workplace")
    val employeeMembers: MutableList<EmployeeMember> = mutableListOf(),

    // 출퇴근 기록
    @OneToMany(mappedBy = "workplace")
    val workRecords: MutableList<WorkRecord> = mutableListOf(),

    // 할 일 리스트
    @OneToMany(mappedBy = "workplace")
    val todos: MutableList<Todo> = mutableListOf(),

    // 할 일 기록
    @OneToMany(mappedBy = "workplace")
    val todoRecords: MutableList<TodoRecord> = mutableListOf(),

    @OneToMany(mappedBy = "workplace")
    val workplaceRequests: MutableList<WorkplaceRequest> = mutableListOf(),

    @Column(columnDefinition = "TEXT", nullable = true)
    val imageUrl: String? = null
) : BaseTimeEntity(id) {

}