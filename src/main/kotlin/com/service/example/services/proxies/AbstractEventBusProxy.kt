package com.service.example.services.proxies

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.core.eventbus.sendAwait
import io.vertx.serviceproxy.ServiceException
import io.vertx.serviceproxy.ServiceExceptionMessageCodec

abstract class AbstractEventBusProxy constructor(
  private val vertx: Vertx,
  private val address: String
) {

  init {
    this.vertx.eventBus()
      .registerDefaultCodec(
        ServiceException::class.java,
        ServiceExceptionMessageCodec()
      )
  }

  fun send(action: String, obj: Any) {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    this.vertx.eventBus().send(this.address, obj, deliveryOptions)
  }

  suspend fun <T> send(action: String, obj: Any): T {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    return this.vertx.eventBus().sendAwait<T>(this.address, obj, deliveryOptions).body()
  }

}
