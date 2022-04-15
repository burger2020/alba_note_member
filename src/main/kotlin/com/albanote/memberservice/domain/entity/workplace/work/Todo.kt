package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import com.albanote.memberservice.domain.entity.workplace.Workplace
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "todo_id"))
class Todo(
    id: Long? = null,

    @CreatedDate
    var createDate: LocalDate? = null,

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var workplace: Workplace? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var title: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String? = null,


    @OneToMany(mappedBy = "todo")
    var chargeEmployee: MutableList<EmployeeTodo> = mutableListOf(),

    @Column(columnDefinition = "TEXT", nullable = true)
    var referenceImageUrl: String? = null,

    // 인증사진 필수 여부
    var isAuthenticationImageRequire: Boolean? = null,

    // 시작, 마감 시간
    var startTime: LocalTime? = null,
    var endTime: LocalTime? = null,

    // 할 일 주기
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = false)
    var todoCycleType: TodoCycleType? = null,

    // 할 일 하는 날
    // 일일 - null / 주간 - (SMTWTFS) ex) 화, 일 -> _1____1 / 월간 1일, 10일, 15일, 30일 -> 1________1____1_______________1_
    @Column(columnDefinition = "TEXT", nullable = true)
    var todoDays: String? = null,
) : BaseEntity(id){
    @JsonIgnore
    var isToday = true
}
