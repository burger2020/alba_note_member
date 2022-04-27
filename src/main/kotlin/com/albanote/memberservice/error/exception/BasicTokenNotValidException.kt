package com.albanote.memberservice.error.exception

/**
 * basic 토큰 유효하지 않음
 * */
class BasicTokenNotValidException(message: String): BaseException(message, code = 610) {
}