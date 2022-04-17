package com.albanote.memberservice.domain.dto.request.workplace

import com.albanote.memberservice.domain.entity.workplace.BusinessCertificationInfo
import com.albanote.memberservice.domain.entity.workplace.Coordinate
import com.albanote.memberservice.domain.entity.workplace.Workplace
import java.time.LocalDate

/**
 * 일터 생성 요청 DTO
 * */
class CreateWorkplaceRequestDTO(
    val bossMemberId: Long,

    val title: String,
    val maxEmployeeNum: Int,
    val address: String,
    val detailAddress: String,
    val commuteRecordWifiMacAddress: String,
    val commuteRecordCoordinate: Coordinate,
    val commuteRecordRadius: Int,

    // 사업장 정보
    val businessLicenseNumber: String?,
    val bossName: String?,
    val openingDate: LocalDate?,
    val tradeName: String?,
    val businessName: String?,

    val imageUrl: String,

    // 사장님 직책 정보
    val bossEmployeeRankName: String,
    val bossEmployeeName: String,
    val bossEmployeePhoneNumber: String,

) {

    fun convertToEntity(): Workplace {
        return Workplace(
            title = title,
            maxEmployeeNum = maxEmployeeNum,
            address = address,
            detailAddress = detailAddress,
            commuteRecordWifiMacAddress = commuteRecordWifiMacAddress,
            commuteRecordCoordinate = commuteRecordCoordinate,
            commuteRecordRadius = commuteRecordRadius,
            businessCertification = BusinessCertificationInfo(
                businessLicenseNumber, bossName, openingDate, tradeName, businessName
            )
        )
    }
}