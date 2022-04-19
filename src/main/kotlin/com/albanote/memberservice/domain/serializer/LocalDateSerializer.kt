package com.albanote.memberservice.domain.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import java.io.IOException
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * json으로 어떻게 바꿀지 설정 날짜 형식을 바꾸기 위해 사용
 *
 */
//@JsonComponent
//class LocalDateSerializer : JsonSerializer<LocalDate?>() {
//    @Throws(IOException::class)
//    override fun serialize(value: LocalDate?, gen: JsonGenerator, serializers: SerializerProvider) {
//        gen.writeNumber(Timestamp.valueOf(LocalDateTime.of(value, LocalTime.of(0, 0))).time)
//    }
//}