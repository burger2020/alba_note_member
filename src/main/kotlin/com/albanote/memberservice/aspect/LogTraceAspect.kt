package com.albanote.memberservice.aspect

import com.albanote.memberservice.annotation.ErrorLogTrace
import com.albanote.memberservice.error.exception.NoLogException
import com.albanote.memberservice.logtrace.LogTrace
import com.albanote.memberservice.logtrace.TraceStatus
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Aspect
@Configuration
class LogTraceAspect(private val logTrace: LogTrace) {

    var slowTime = 1000

    @Around("@annotation(errorLogTrace)")
    fun executeBySlowLogicAnnotation(joinPoint: ProceedingJoinPoint, errorLogTrace: ErrorLogTrace): Any? {
        slowTime = errorLogTrace.slowTime
        val result = execute(joinPoint)
        slowTime = 1000
        return result
    }

    @Around("allController() || allService() || allRepository()")
    fun execute(joinPoint: ProceedingJoinPoint): Any? {
        var status: TraceStatus? = null
        var argString = ""
        joinPoint.args.forEach { argString += (", $it") }
        argString = argString.replaceFirst(", ", "")
        val prefix =
            getLogPrefix(joinPoint.`this`.javaClass, joinPoint.signature.toShortString()).replace("..", argString)
        try {
            status = logTrace.begin(prefix)

            val result = joinPoint.proceed()

            logTrace.end(status, slowTime)

            return result
        } catch (e: Exception) {
            if (e is NoLogException) {
                status?.message += "  [${e::class.java.simpleName}]: ${e.message}"
                status?.status = TraceStatus.CLIENT_ERROR
                logTrace.end(status!!)
            } else {
                status?.status = TraceStatus.SERVER_ERROR
                logTrace.exception(status!!, e)
            }
            throw e
        }
    }

    private fun getLogPrefix(javaClass: Class<Any>, toShortString: String): String {
        return "[${getTargetType(javaClass)}] [${toShortString}]"
    }

    private fun getTargetType(javaClass: Class<Any>): String {
        return when {
            AnnotationUtils.findAnnotation(javaClass, Controller::class.java) != null -> "Controller"
            AnnotationUtils.findAnnotation(javaClass, Service::class.java) != null -> "Service"
            AnnotationUtils.findAnnotation(javaClass, Repository::class.java) != null -> "Repository"
            else -> ""
        }
    }

    @Pointcut("execution(* com.sanho.hitup.memberservice.controller.app..*(..))")
    fun allController() {
    }

    @Pointcut("execution(* com.sanho.hitup.memberservice.service.app..*(..))")
    fun allService() {
    }

    @Pointcut("execution(* com.sanho.hitup.memberservice.repository.app..*(..))")
    fun allRepository() {
    }

    @Pointcut("execution(* com.sanho.hitup.memberservice.client.*ServiceClient.*(..))")
    fun allFeignClient() {
    }
}