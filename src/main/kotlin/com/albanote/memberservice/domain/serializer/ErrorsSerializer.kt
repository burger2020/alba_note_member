package com.albanote.memberservice.domain.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import org.springframework.validation.Errors
import java.util.function.Consumer

import kotlin.Throws
import java.io.IOException
import org.springframework.boot.jackson.JsonComponent
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

@JsonComponent
class ErrorsSerializer : JsonSerializer<Errors>() {
    @Throws(IOException::class)
    override fun serialize(errors: Errors, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeStartObject()
        jsonGenerator.writeArrayFieldStart("errors")
        errors.fieldErrors.stream().forEach { e: FieldError ->
            try {
                jsonGenerator.writeStartObject()
                jsonGenerator.writeStringField("field", e.field)
                jsonGenerator.writeStringField("code", e.code)
                jsonGenerator.writeStringField("message", e.defaultMessage)
                val rejectedValue = e.rejectedValue
                if (rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString())
                }
                jsonGenerator.writeEndObject()
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }
        errors.globalErrors.forEach(Consumer { e: ObjectError ->
            try {
                jsonGenerator.writeStartObject()
                jsonGenerator.writeStringField("field", null)
                jsonGenerator.writeStringField("objectName", e.objectName)
                jsonGenerator.writeStringField("code", e.code)
                jsonGenerator.writeStringField("message", e.defaultMessage)
                jsonGenerator.writeEndObject()
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        })
        jsonGenerator.writeEndArray()
        jsonGenerator.writeEndObject()
    }
}