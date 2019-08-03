package com.service.example.services

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

interface OrderService {

  suspend fun send(order: Long)
  suspend fun confirm(orderId: Long)
  suspend fun cancel(orderId: Long)
  suspend fun track(orderId: Long) : JsonObject

  companion object {
    fun create(vertx: Vertx): OrderService {
      return OrderServiceImpl(vertx)
    }
    fun createProxy(vertx: Vertx): OrderService {
      return OrderServiceEBProxy(vertx, ADDRESS)
    }
    const val ADDRESS = "order-service"
  }

}
