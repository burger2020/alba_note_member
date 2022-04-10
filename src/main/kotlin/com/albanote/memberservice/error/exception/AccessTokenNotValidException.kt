package com.albanote.memberservice.error.exception

/**
 * access 토큰 유효하지 않음
 * */
class AccessTokenNotValidException(message: String): BaseException(message, code = 600) {
}