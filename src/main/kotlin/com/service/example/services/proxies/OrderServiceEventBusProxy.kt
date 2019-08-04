package com.service.example.services.proxies

import com.service.example.models.Order
import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class OrderServiceEventBusProxy(vertx: Vertx, address: String) : OrderService, AbstractEventBusProxy(vertx, address) {

  override suspend fun list() : List<Order> {
    return Order.fromJsonArray(this.send<JsonArray>("list", JsonObject()))
  }

  override suspend fun create(order: Order) : Order {
    return Order.fromJson(this.send<JsonObject>("create", order.toJson()))
  }

  override suspend fun read(id: Long) : Order {
    return Order.fromJson(this.send<JsonObject>("read", JsonObject.mapFrom(mapOf("id" to id))))
  }

  override suspend fun update(order: Order): Order {
    return Order.fromJson(this.send<JsonObject>("update", order.toJson()))
  }

  override suspend fun delete(id: Long) : Order {
    return Order.fromJson(this.send<JsonObject>("delete", JsonObject.mapFrom(mapOf("id" to id))))
  }

}
