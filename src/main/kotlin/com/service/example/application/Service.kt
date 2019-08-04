package com.service.example.application

import com.service.example.services.OrderService
import io.vertx.kotlin.coroutines.CoroutineVerticle

class Service : CoroutineVerticle() {
  override suspend fun start() {
    OrderService.createHandler(vertx)
  }
}
