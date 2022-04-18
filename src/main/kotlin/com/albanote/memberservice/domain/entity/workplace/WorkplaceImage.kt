package com.albanote.memberservice.domain.entity.workplace

import com.albanote.memberservice.domain.entity.BaseEntity
import javax.persistence.*

/**
 * 일터 이미지
 * */
@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_image_id"))
class WorkplaceImage(
    id: Long? = null,

    @JoinColumn(name = "workplace_id")
    @OneToOne(fetch = FetchType.LAZY)
    val workplace: Workplace? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    val imageUrl: String? = null
) : BaseEntity(id) {
}