package com.albanote.memberservice.domain.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.boot.jackson.JsonComponent
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

//@JsonComponent
//class LocalDateDeserializer : JsonDeserializer<LocalDate>() {
//    @Throws(IOException::class)
//    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate {
//        return LocalDateTime.ofInstant(
//            Instant.ofEpochMilli(p.longValue),
//            TimeZone.getDefault().toZoneId()
//        ).toLocalDate()
//    }
//}