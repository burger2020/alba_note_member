package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*


@Entity
@AttributeOverride(name = "id", column = Column(name = "work_record_id"))
class WorkRecord(
    id: Long? = null,

    // todo 할 일, 직책 정보 변경 시 어떻게 대응?
    // 현재는 할 일, 근무 내역이 모두 남기는게 아니고 완료된것만 남기므로 할 일, 직책 정보에서 등록되지 않은 데이터 유추해서 가져오고 있음...
    // 수정에 깔끔하게 대응하는방법은 매번 모든 할 일, 근무 내역을 남기는 방법인데 이럴경우 불필요한 데이터가 너무 쌓이게 되고
    // 그럴바엔 수정을 할 때 기존 정보를 오래된 데이터로 보내고 최신데이터로 사용하게?
    // 근데 직원의 직책이 바뀌면 과거 직책이 안 남음으로 유추할 수 없음... 다 남겨야하나...

    @JoinColumn(name = "employee_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var employeeMember: EmployeeMember? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

    // 근무 유형
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    var workType: WorkType? = null,

    // 근무 날짜
    val workDate: LocalDate? = null,
    // 출퇴근 시간
    val officeGoingTime: LocalTime? = null,
    val quittingTime: LocalTime? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    val memo: String? = null
) : BaseEntity(id) {

}
