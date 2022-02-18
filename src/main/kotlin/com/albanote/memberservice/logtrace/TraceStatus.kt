package com.albanote.memberservice.logtrace

import com.albanote.memberservice.logtrace.TraceId

/**
 * 로그 전체 상태
 *
 * @author burger
 * */
class TraceStatus(
    val traceId: TraceId,
    val startTimeMs: Long,
    var message: String,
    var status: String
) {
    companion object {
        const val TRACE = "TRACE"
        const val SLOW_LOGIC = "SLOW_LOGIC"
        const val SERVER_ERROR = "SERVER_ERROR"
        const val CLIENT_ERROR = "CLIENT_ERROR"
    }

    fun getResultTime(): Long {
        return System.currentTimeMillis() - startTimeMs
    }
}