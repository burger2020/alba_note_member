package com.albanote.memberservice.error.exception.workplace

import com.albanote.memberservice.error.exception.BaseException

/**
 * 근무 기록 없음
 * errorCode - 700
 *
 * @author burger
 * */
class NotFoundWorkRecordException(message: String = "근무 내역 없음") : BaseException(message, code = 700)