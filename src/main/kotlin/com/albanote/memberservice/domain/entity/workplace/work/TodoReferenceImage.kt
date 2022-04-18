package com.albanote.memberservice.domain.entity.workplace.work

import com.albanote.memberservice.domain.entity.BaseEntity
import javax.persistence.*

/**
 * 할일 참조 사진
 * */
@Entity
@AttributeOverride(name = "id", column = Column(name = "todo_reference_image_id"))
class TodoReferenceImage(
    id: Long? = null,

    @JoinColumn(name = "todo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val todo: Todo? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val imageUrl: String? = null
) : BaseEntity(id) {
}