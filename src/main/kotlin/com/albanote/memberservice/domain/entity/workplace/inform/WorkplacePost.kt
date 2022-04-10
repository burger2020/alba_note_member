package com.albanote.memberservice.domain.entity.workplace.inform

import com.albanote.memberservice.domain.entity.BaseTimeEntity
import com.albanote.memberservice.domain.entity.member.Member
import javax.persistence.*

/**
 * 일터 공지
 * */
@Entity
@AttributeOverride(name = "id", column = Column(name = "workplace_inform_id"))
class WorkplacePost(
    id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val title: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String? = null,

    @OneToMany(mappedBy = "inform")
    val imageUrls: MutableList<WorkplacePostImage> = mutableListOf(),

    @JoinColumn(name = "created_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val informCreatedMember: Member? = null,

    //공지 상단 고정 여부
    val isFix: Boolean? = null,

    // 근무 지급 방식
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = true)
    val workplacePostType: WorkplacePostType? = null,

    @JoinColumn(name = "workplace_inform_views_id")
    @OneToOne(fetch = FetchType.LAZY)
    val views: WorkplacePostViews? = null
) : BaseTimeEntity(id) {
}