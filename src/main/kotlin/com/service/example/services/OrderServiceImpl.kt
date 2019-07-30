package com.service.example.services

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import org.slf4j.LoggerFactory

class OrderServiceImpl(vertx: Vertx) : OrderService, ServiceProxyHandler(vertx, "orders") {

  override suspend fun onEvent(action: String, body: JsonObject): JsonObject {
    val orderId = body.getLong("id")
    when(action) {
      "track" -> track(orderId)
    }
    return JsonObject.mapFrom("result" to "default")
  }

  var client: WebClient = WebClient.create(vertx)

  override suspend fun send(order: Long) {
    //val response = client.post("", "/delivery").sendJsonAwait(order)
    //val json = response.bodyAsJsonObject()
  }

  override suspend fun confirm(orderId: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun cancel(orderId: Long) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun track(orderId: Long) : JsonObject {
    logger.info("Llego mensaje")
    return JsonObject().put("value", 10L)
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
  }
}
