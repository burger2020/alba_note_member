package com.albanote.memberservice.domain.entity.workplace

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * 사업자 인증 정보
 * */
@Embeddable
class BusinessCertificationInfo(

    // 사업자 번호
    @Column(columnDefinition = "TEXT", nullable = true)
    var businessLicenseNumber: String? = null,

    // 대표자 성명
    @Column(columnDefinition = "TEXT", nullable = true)
    var bossName: String? = null,

    // 개업 일자
    @Column(columnDefinition = "TEXT", nullable = true)
    var openingDate: LocalDate? = null,

    // 상호명
    @Column(columnDefinition = "TEXT", nullable = true)
    var tradeName: String? = null,

    // 업태명
    @Column(columnDefinition = "TEXT", nullable = true)
    var businessName: String? = null
)