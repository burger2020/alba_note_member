package com.albanote.memberservice.domain.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.boot.jackson.JsonComponent
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

//@JsonComponent
//class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
//    @Throws(IOException::class, JsonProcessingException::class)
//    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
//        return LocalDateTime.ofInstant(
//            Instant.ofEpochMilli(p.longValue),
//            TimeZone.getDefault().toZoneId()
//        )
//    }
//}