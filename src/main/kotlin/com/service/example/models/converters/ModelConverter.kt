package com.service.example.models.converters

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.streams.toList

object ModelConverter {

  inline fun <reified T>fromJson(json: JsonObject) : T {
    return json.mapTo(T::class.java)
  }

  inline fun <reified T>fromJson(list: List<JsonObject>) : List<T> {
    return list.stream().map { fromJson<T>(it as JsonObject) }.toList()
  }

  inline fun <reified T>fromJson(jsonArray: JsonArray) : List<T> {
    return jsonArray.stream().map { fromJson<T>(it as JsonObject) }.toList()
  }

  inline fun <reified T>toJson(model: T) : JsonObject {
    return JsonObject.mapFrom(model)
  }

  inline fun<reified T>toJson(list: List<T>) : JsonArray {
    return JsonArray(list.stream().map { toJson(it) }.toList())
  }

}
