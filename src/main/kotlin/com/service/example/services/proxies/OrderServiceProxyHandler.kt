package com.service.example.services.proxies

import com.service.example.models.Order
import com.service.example.models.converters.ModelConverter
import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.lang.IllegalStateException

class OrderServiceProxyHandler(vertx: Vertx, val service: OrderService) : AbstractProxyHandler(vertx) {
  override suspend fun handle(action: String, message: JsonObject): Any {
    return when (action) {
      "list" -> {
        ModelConverter.toJson(service.list())
      }
      "create" -> {
        val order = ModelConverter.fromJson<Order>(message)
        ModelConverter.toJson(service.create(order))
      }
      "read" -> {
        val id = message.getString("id")
        ModelConverter.toJson(service.read(id))
      }
      "update" -> {
        val order = ModelConverter.fromJson<Order>(message)
        ModelConverter.toJson(service.update(order))
      }
      "delete" -> {
        val id = message.getString("id")
        ModelConverter.toJson(service.delete(id))
      }
      else -> throw throw IllegalStateException("action $action is not valid")
    }
  }
}
