package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "todo_record_image_id"))
class TodoRecordImage(
    id: Long? = null,

    @JoinColumn(name = "todo_record_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var todoRecord: TodoRecord? = null,

    val imageUrl: String? = null
) : BaseEntity(id) {
}