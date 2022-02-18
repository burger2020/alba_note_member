package com.albanote.memberservice.annotation

/**
 * 페인클라이언트 실패시 재시도, 실패 시 로그?
 *
 * @param retryCount - 재시도 횟수
 * @author burger
 * */
annotation class RetryFeignClient(
    val retryCount: Int = 3 // default
)
