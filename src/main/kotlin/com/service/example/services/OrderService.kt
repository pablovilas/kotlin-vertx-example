package com.service.example.services

import io.vertx.core.json.JsonObject

interface OrderService {
  suspend fun send(order: Long)
  suspend fun confirm(orderId: Long)
  suspend fun cancel(orderId: Long)
  suspend fun track(orderId: Long) : JsonObject
}
