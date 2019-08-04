package com.service.example.models

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.streams.toList

data class Order(
  var id: Long = 0,
  var total: Long = 0,
  var user: String? = null
) {

  fun toJson() : JsonObject {
    return JsonObject.mapFrom(this)
  }

  companion object {
    fun fromJson(json: JsonObject) : Order {
      return json.mapTo(Order::class.java)
    }
    fun fromJsonArray(jsonArray: JsonArray) : List<Order> {
      return jsonArray.stream().map { fromJson(it as JsonObject) }.toList()
    }
    fun toJson(order: Order) : JsonObject {
      return order.toJson()
    }
  }

}
