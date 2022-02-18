package com.albanote.memberservice.logtrace

import java.util.*

/**
 * 로그 랜덤 아이디 및 레벨 저장
 *
 * @author burger
 * */
class TraceId(
    val id: String = UUID.randomUUID().toString().substring(0, 8),
    val level: Int = 0,
) {
    fun createNextId(): TraceId {
        return TraceId(id, level + 1)
    }

    fun createPreviousId(): TraceId {
        return TraceId(id, level - 1)
    }

    fun isFirstLevel(): Boolean {
        return level == 0
    }
}