package com.albanote.memberservice.security

import com.albanote.memberservice.error.exception.BaseException

/**
 * 존재하지 않는 아이디
 *
 * error code - 602
 * */
class NotExistMemberIdException(message: String) : BaseException(message, code = 602)