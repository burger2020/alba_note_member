package com.albanote.memberservice.error.exception

/**
 * 차단된 멤버 접근
 * errorCode - 600
 *
 * @author burger
 * */
class BlindMemberAccessException(message: String): BaseException(message, code = 600) {

}