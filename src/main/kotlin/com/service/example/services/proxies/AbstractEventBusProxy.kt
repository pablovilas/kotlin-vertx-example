package com.service.example.services.proxies

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.core.eventbus.requestAwait
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

  suspend fun <T> send(action: String, obj: Any): T {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    return this.vertx.eventBus().requestAwait<T>(this.address, obj, deliveryOptions).body()
  }

}
