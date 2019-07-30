package com.service.example.controllers

import com.service.example.services.EventBusProxy
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory


class OrderController(vertx: Vertx) : EventBusProxy(vertx, "orders") {

  suspend fun create(context: RoutingContext) {
    val result = send<JsonObject>("create", context.bodyAsJson)
    context.response().end("Result $result")
  }

  suspend fun confirm(context: RoutingContext) {

  }

  suspend fun cancel(context: RoutingContext) {

  }

  suspend fun track(context: RoutingContext) {
    logger.info("Atiendo")
    val orderId = context.request().getParam("id").toLong()
    val result = send<JsonObject>("track", mapOf("id" to orderId))
    context.response().end("Result $result")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)
  }
}
