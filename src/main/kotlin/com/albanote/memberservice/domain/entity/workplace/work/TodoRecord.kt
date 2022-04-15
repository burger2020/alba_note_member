package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "todo_record_id"))
class TodoRecord(
    id: Long? = null,

    @JoinColumn(name = "employee_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var employeeMember: EmployeeMember? = null,

    @JoinColumn(name = "todo_id")
    @OneToOne(fetch = FetchType.LAZY)
    var todo: Todo? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

    @OneToMany(mappedBy = "todoRecord")
    var todoRecordImages: MutableList<TodoRecordImage> = mutableListOf(),

    // 근무 날짜
    val todoDate: LocalDate? = null,
    // 완료 시간
    val CompletionTime: LocalTime? = null,
) : BaseEntity(id) {
}