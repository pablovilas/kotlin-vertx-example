package com.service.example.services.proxies

import com.service.example.models.converters.ModelConverter
import com.service.example.models.Order
import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class OrderServiceEventBusProxy(vertx: Vertx, address: String) : OrderService, AbstractEventBusProxy(vertx, address) {

  override suspend fun list() : List<Order> {
    return ModelConverter.fromJson(this.send("list", JsonObject()) as JsonArray)
  }

  override suspend fun create(order: Order) : Order {
    return ModelConverter.fromJson(this.send("create", ModelConverter.toJson(order)) as JsonObject)
  }

  override suspend fun read(id: String) : Order {
    return ModelConverter.fromJson(this.send("read", JsonObject.mapFrom(mapOf("id" to id))) as JsonObject)
  }

  override suspend fun update(order: Order): Order {
    return ModelConverter.fromJson(this.send("update", ModelConverter.toJson(order)) as JsonObject)
  }

  override suspend fun delete(id: String) : Order {
    return ModelConverter.fromJson(this.send("delete", JsonObject.mapFrom(mapOf("id" to id))) as JsonObject)
  }

}
