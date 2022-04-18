package com.albanote.memberservice.domain.dto

import com.querydsl.core.annotations.QueryProjection

class QueryDslTripleDTO @QueryProjection constructor(
    val first: Any?,
    val second: Any?,
    val third: Any?
) {
}