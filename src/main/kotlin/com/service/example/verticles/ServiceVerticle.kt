package com.service.example.verticles

import io.vertx.kotlin.coroutines.CoroutineVerticle

class ServiceVerticle : CoroutineVerticle() {
  override suspend fun start() {
    //ServiceBinder(vertx)
    //  .setAddress("order-service")
    //  .register(OrderService::class.java, OrderService.OrderServiceFactory.create(vertx))
  }
}
