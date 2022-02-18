package com.albanote.memberservice.error.exception

/**
 * 차단된 멤버 접근
 * errorCode - 602
 *
 * @author burger
 * */
class SocialIdNotValidException(message: String): HitupException(message, code = 602) {
}