package com.albanote.memberservice.logtrace

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThreadLocalLogTrace : LogTrace {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private var traceIdHolder: ThreadLocal<TraceId?> = ThreadLocal()
    private val logs: ThreadLocal<MutableList<String>> = ThreadLocal()
    private var latestException: ThreadLocal<Throwable?> = ThreadLocal()
    private var status: ThreadLocal<String> = ThreadLocal()

    override fun begin(message: String): TraceStatus {
        syncTraceId()
        val traceId = traceIdHolder.get()
        val startTimeMs = System.currentTimeMillis()

        val log = "${addSpace(START_PREFIX, traceId!!.level)}$message"
        logs.get().add(log)
        return TraceStatus(traceId, startTimeMs, message, TraceStatus.TRACE)
    }

    private fun syncTraceId() {
        if (traceIdHolder.get() == null) {
            traceIdHolder.set(TraceId())
            logs.set(mutableListOf())
        } else traceIdHolder.set(traceIdHolder.get()!!.createNextId())
    }

    override fun end(status: TraceStatus, slowTime: Int) {
        if (slowTime > status.getResultTime()) {
            val space = addSpace(COMPLETE_PREFIX, status.traceId.level)
            logs.get().add("$space${status.message}  ${status.getResultTime()}ms")
            releaseTraceId(status.status)
        } else {
            slow(status)
        }
    }

    override fun exception(status: TraceStatus, e: Exception) {
        val space = addSpace(EX_PREFIX, status.traceId.level)
        logs.get().add("$space${status.message}  ${status.getResultTime()}ms  Exception - ${e::class.java}")
        latestException.set(e)
        releaseTraceId(status.status)
    }

    override fun slow(status: TraceStatus) {
        val space = addSpace(SLOW_PREFIX, status.traceId.level)
        logs.get().add("$space${status.message}  ${status.getResultTime()}ms")
        status.status = TraceStatus.SLOW_LOGIC
        releaseTraceId(status.status)
    }

    private fun releaseTraceId(status: String) {
        val traceId = traceIdHolder.get()
        if (traceId?.isFirstLevel() == true) { // 종료 -> 로그 남기기
            sendLog(traceId, status)
            traceIdHolder.remove()
            logs.remove()
            latestException.remove()
        } else traceIdHolder.set(traceId?.createPreviousId())
    }

    private fun sendLog(traceId: TraceId, status: String) {
        if (latestException.get() == null) {
            var log = "\n[${traceId.id}] - $status\n"
            logs.get().forEach { log += it + "\n" }
            logger.info(log)
        } else {
            var log = "[${traceId.id}]\n"
            logs.get().forEach { log += it + "\n" }

            log += ("[Exception] " + removeOrderPackageLogs())
            logger.error(log)
        }
    }

    private fun removeOrderPackageLogs(): List<String> {
        val stackTrace = latestException.get()!!.stackTraceToString().split("\n")
        return stackTrace.filter { it.contains("com.sanho.hitup.") || it.contains("Exception") }.map { it.replace("\tat com.sanho.hitup.", "") }.distinct()
    }

    private fun addSpace(prefix: String, level: Int): String {
        val sb = StringBuilder()
        for (i in 0 until level) {
            sb.append(if (i == level - 1) "|$prefix" else "|   ")
        }
        return sb.toString()
    }

    companion object {
        private const val START_PREFIX = "-->"
        private const val COMPLETE_PREFIX = "<--"
        private const val SLOW_PREFIX = "<~~"
        private const val EX_PREFIX = "<X-"
    }
}