package com.albanote.memberservice.error.exception

open class HitupException(message: String, val code: Int) : Exception(message), NoLogException