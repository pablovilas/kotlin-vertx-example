package com.service.example.services

import com.service.example.services.proxies.AbstractVertxProxyHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class OrderServiceVertxProxyHandler(val vertx: Vertx, val service: OrderService, val b: Boolean, val t : Long) : AbstractVertxProxyHandler<OrderService>(vertx, service) {

  override suspend fun handle(action: String, message: JsonObject) : Any {
    when(action) {
      "track" -> {
        logger.info("OrderServiceVertxProxyHandler::track")
        return service.track(10L)
      }
    }
    return 1L
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderServiceVertxProxyHandler::class.java)
  }

}
