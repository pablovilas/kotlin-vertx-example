package com.service.example.verticles

import com.service.example.services.OrderService
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.serviceproxy.ServiceBinder

class ServiceVerticle : CoroutineVerticle() {
  override suspend fun start() {
    ServiceBinder(vertx)
      .setAddress(OrderService.ADDRESS)
      .register(OrderService::class.java, OrderService.create(vertx))
  }
}
