package com.albanote.memberservice.domain.entity.workplace.inform

import com.albanote.memberservice.domain.entity.BaseEntity
import javax.persistence.*

@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_inform_image_id"))
class WorkplacePostImage(
    id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val url: String? = null,

    @JoinColumn(name = "workplace_inform_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val inform: WorkplacePost? = null
) : BaseEntity(id) {
}