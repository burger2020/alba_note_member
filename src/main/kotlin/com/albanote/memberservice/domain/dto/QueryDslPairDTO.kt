package com.albanote.memberservice.domain.dto

import com.querydsl.core.annotations.QueryProjection

class QueryDslPairDTO @QueryProjection constructor(
    val first: Any?,
    val second: Any?
) {
}