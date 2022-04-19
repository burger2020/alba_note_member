package com.albanote.memberservice.error.exception.workplace

import com.albanote.memberservice.error.exception.BaseException

/**
 * 존재하지 않는 일터
 * errorCode - 701
 *
 * @author burger
 * */
class NotExistWorkplaceException(message: String = "존재하지 않는 일터입니다.") : BaseException(message, code = 701) {
}