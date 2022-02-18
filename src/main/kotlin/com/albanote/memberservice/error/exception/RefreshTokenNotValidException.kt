package com.albanote.memberservice.error.exception

class RefreshTokenNotValidException(message: String): HitupException(message, code = 410) {
}