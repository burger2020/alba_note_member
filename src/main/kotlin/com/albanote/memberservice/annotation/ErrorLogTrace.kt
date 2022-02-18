package com.albanote.memberservice.annotation

/**
 * LogTraceAspect 시간 초과 로그 값 변경
 *
 * @param slowTime - 시간 초과 기준 ms
 * @author burger
 * */
annotation class ErrorLogTrace(val slowTime: Int = 1000) {
}