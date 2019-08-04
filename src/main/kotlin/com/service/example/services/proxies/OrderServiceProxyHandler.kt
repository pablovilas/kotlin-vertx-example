package com.service.example.services.proxies

import com.service.example.models.Order
import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.lang.IllegalStateException
import kotlin.streams.toList

class OrderServiceProxyHandler(vertx: Vertx, val service: OrderService) : AbstractProxyHandler(vertx) {
  override suspend fun handle(action: String, message: JsonObject): Any {
    when (action) {
      "list" -> {
        return JsonArray(service.list().stream().map { Order.toJson(it) }.toList())
      }
      "create" -> {
        val order = Order.fromJson(message)
        return service.create(order).toJson()
      }
      "read" -> {
        val id = message.getLong("id")
        return service.read(id).toJson()
      }
      "update" -> {
        val order = Order.fromJson(message)
        return service.update(order).toJson()
      }
      "delete" -> {
        val id = message.getLong("id")
        return service.delete(id).toJson()
      }
    }
    throw throw IllegalStateException("action $action is not valid")
  }
}
