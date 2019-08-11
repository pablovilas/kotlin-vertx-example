package com.service.example.clients

import java.io.IOException
import java.io.ObjectOutputStream
import java.io.ByteArrayOutputStream

class MessageAttribute(
  private var name: String,
  private var type: String
) {

  private lateinit var value: String

  init {
    build(this.name, this.value)
  }

  fun getName(): String {
    return name
  }

  fun getValue(): String {
    return value
  }

  fun getType(): String {
    return type
  }

  private fun build(name: String, value: Any) {
    this.name = name
    when {
      isString(value) -> {
        this.type = "String"
        this.value = value.toString()
      }
      isNumber(value) -> {
        this.type = "Number"
        this.value = value.toString()
      }
      else -> {
        this.type = "Binary"
        this.value = convertToBinary(value)
      }
    }
  }

  private fun isString(value: Any): Boolean {
    return value is String
  }

  private fun isNumber(value: Any): Boolean {
    if (value is Number) {
      return true
    }
    if (value.javaClass.isPrimitive) {
      return when (value.javaClass.name) {
        "void" -> false
        "boolean" -> false
        else -> true
      }
    }
    return false
  }

  private fun convertToBinary(obj: Any): String {
    try {
      ByteArrayOutputStream().use { outputStream ->
        ObjectOutputStream(outputStream).use { output ->
          output.writeObject(obj)
          return outputStream.toString()
        }
      }
    } catch (ex: IOException) {
      return ""
    }

  }

}
