//package com.albanote.memberservice.aspect
//
//import com.albanote.memberservice.annotation.RetryFeignClient
//import io.github.resilience4j.circuitbreaker.CallNotPermittedException
//import org.aspectj.lang.ProceedingJoinPoint
//import org.aspectj.lang.annotation.Around
//import org.aspectj.lang.annotation.Aspect
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
//import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
//import org.springframework.cloud.client.circuitbreaker.ConfigBuilder
//import org.springframework.stereotype.Component
//
///**
// * feign client 메서드 실패 시 재시도 및 로깅
// *
// * @author burger
// * */
//@Aspect
//@Component
//class HitupCircuitBreakerAspect<CONF, CONFB : ConfigBuilder<CONF>?>(
//    private val circuitBreakerFactory: CircuitBreakerFactory<CONF, CONFB>
//) {
//
//    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
//
//    //    @Around("execution(* com.sanho.hitup.communityservice.client.service.FeignClientServiceImpl(..))")
//    @Around("@annotation(retry)")
//    fun doCircuitBreakerRun(joinPoint: ProceedingJoinPoint, retry: RetryFeignClient): Any? {
//        val circuitBreaker: CircuitBreaker = circuitBreakerFactory.create("circuitbreaker")
//        val startTime = System.currentTimeMillis()
//        var result: Any? = null
//
//        var isBreak = false
//        var latestThrowable: Throwable? = null
//
//        var argString = ""
//        joinPoint.args.forEach { argString += (", $it") }
//        argString = argString.replaceFirst(", ", "")
//        val name = "\n[FeignClient]  " + joinPoint.signature.toShortString().replace("..", argString)
//
//        for (i in 0 until retry.retryCount) {
//            circuitBreaker.run({
//                result = joinPoint.proceed()
//                isBreak = true
//            }, {
//                // circuit breaker 열림 상태 접근
//                if (it::class.java == CallNotPermittedException::class.java) {
//                    isBreak = true
//                    return@run
//                }
//                val resultTime = System.currentTimeMillis() - startTime
//                if (i < retry.retryCount)
//                    log.info("$name - RETRY ${i + 1}/${retry.retryCount}  ${resultTime}ms\n[Exception] - ${it.stackTraceToString()}")
//                latestThrowable = it
//            })
//            if (isBreak) break
//        }
//
//        val resultTime = System.currentTimeMillis() - startTime
//        if (result == null && latestThrowable != null)
//            log.error("$name - ERROR Over retry  ${resultTime}ms\n[Exception] - ${latestThrowable?.stackTraceToString()}")
//        else
//            log.trace("$name - TRACE ${resultTime}ms\n")
//        return result
//    }
//}