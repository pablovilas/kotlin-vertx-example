package com.service.example.controllers

import com.service.example.services.OrderService
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory


class OrderController(vertx: Vertx) {

  val orderService = OrderService.createProxy(vertx)

  suspend fun create(context: RoutingContext) {
    //val result = send<JsonObject>("create", context.bodyAsJson)
    //context.response().end("Result $result")
  }

  suspend fun confirm(context: RoutingContext) {

  }

  suspend fun cancel(context: RoutingContext) {

  }

  suspend fun track(context: RoutingContext) {
    logger.info("OrderController::track")
    val orderId = context.request().getParam("id").toLong()
    val result = orderService.track(orderId)
    context.response().end("Result $result")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)
  }
}
