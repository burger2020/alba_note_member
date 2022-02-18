package com.albanote.memberservice.error.handler

import com.albanote.memberservice.domain.dto.ErrorDTO
import com.albanote.memberservice.error.exception.HitupException
import com.albanote.memberservice.error.exception.RefreshTokenNotValidException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    /**
     * refresh token 에로는 401
     * 그 외 403
     *
     * @author burger
     * */
    @ExceptionHandler(value = [HitupException::class])
    fun totalHitupException(e: HitupException): ResponseEntity<ErrorDTO> {
        val errorDTO = ErrorDTO(message = e.message, code = e.code)
        val status = if (e is RefreshTokenNotValidException) HttpStatus.UNAUTHORIZED else HttpStatus.FORBIDDEN
        return ResponseEntity.status(status).body(errorDTO)
    }
}