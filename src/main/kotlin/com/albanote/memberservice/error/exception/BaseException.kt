package com.albanote.memberservice.error.exception

open class BaseException(message: String, val code: Int) : Exception(message), NoLogException