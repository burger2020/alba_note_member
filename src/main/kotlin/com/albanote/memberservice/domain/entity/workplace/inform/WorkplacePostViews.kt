package com.albanote.memberservice.domain.entity.workplace.inform

import com.albanote.memberservice.domain.entity.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity


@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_inform_views_id"))
class WorkplacePostViews(
    id: Long? = null,

    val view: Int? = null
): BaseEntity(id) {
}