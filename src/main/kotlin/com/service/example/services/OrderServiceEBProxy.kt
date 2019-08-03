package com.service.example.services

import com.service.example.services.proxies.AbstractVertxEBProxy
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class OrderServiceEBProxy(val vertx: Vertx, val address: String) : OrderService, AbstractVertxEBProxy(vertx, address) {
  override suspend fun confirm(orderId: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun cancel(orderId: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun track(orderId: Long): JsonObject {
    logger.info("OrderServiceEBProxy::track")
    return this.send<JsonObject>("track", JsonObject.mapFrom(mapOf("orderId" to orderId)))
  }

  override suspend fun send(order: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderServiceEBProxy::class.java)
  }
}
