package com.service.example.services.proxies

import com.service.example.models.Order
import com.service.example.models.converters.ModelConverter
import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.lang.IllegalStateException

class OrderServiceProxyHandler(vertx: Vertx, val service: OrderService) : AbstractProxyHandler(vertx) {
  override suspend fun handle(action: String, message: JsonObject): Any {
    when (action) {
      "list" -> {
        return ModelConverter.toJson(service.list())
      }
      "create" -> {
        val order = ModelConverter.fromJson<Order>(message)
        return ModelConverter.toJson(service.create(order))
      }
      "read" -> {
        val id = message.getString("id")
        return ModelConverter.toJson(service.read(id))
      }
      "update" -> {
        val order = ModelConverter.fromJson<Order>(message)
        return ModelConverter.toJson(service.update(order))
      }
      "delete" -> {
        val id = message.getString("id")
        return ModelConverter.toJson(service.delete(id))
      }
    }
    throw throw IllegalStateException("action $action is not valid")
  }
}
