package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "employee_todo_id"))
class EmployeeTodo(
    id: Long? = null,

    @JoinColumn(name = "employee_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val employeeMember: EmployeeMember? = null,

    @JoinColumn(name = "todo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val todo: Todo? = null
) : BaseEntity(id)