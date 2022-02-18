package com.albanote.memberservice.domain.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.Throws
import java.io.IOException
import org.springframework.boot.jackson.JsonComponent
import com.fasterxml.jackson.databind.SerializerProvider

@JsonComponent
class LocalDateTimeSerializer : JsonSerializer<LocalDateTime?>() {
    @Throws(IOException::class)
    override fun serialize(value: LocalDateTime?, gen: JsonGenerator, serializers: SerializerProvider) {
//        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toEpochSecond());
        gen.writeNumber(Timestamp.valueOf(value).time)
    }
}