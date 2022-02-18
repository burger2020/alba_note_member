package com.albanote.memberservice.logtrace

/**
 * 로그 남기기용 추상
 *
 * @author burger
 * */
interface LogTrace {

    fun begin(message: String): TraceStatus

    fun slow(status: TraceStatus)

    fun end(status: TraceStatus, slowTime: Int = 99999)

    fun exception(status: TraceStatus, e: Exception)
}