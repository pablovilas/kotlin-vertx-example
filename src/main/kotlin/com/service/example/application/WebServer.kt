package com.service.example.application

import com.service.example.controllers.OrderController
import io.vertx.ext.web.Router

class WebServer : Server() {

  private lateinit var orderController : OrderController
  private lateinit var orderControllerEventBus : OrderController

  override fun initializeControllers() {
    orderController = OrderController(vertx)
    orderControllerEventBus = OrderController(vertx, usesEventBus = true)
  }

  override fun handleRoutes(router: Router) {
    // V1: Direct service method call
    router.post("/v1/orders").handlerAwait(orderController::create)
    router.get("/v1/orders").handlerAwait(orderController::list)
    router.get("/v1/orders/:id").handlerAwait(orderController::read)
    router.put("/v1/orders").handlerAwait(orderController::update)
    router.delete("/v1/orders/:id").handlerAwait(orderController::delete)
    // V2: Event bus service method call
    router.post("/v2/orders").handlerAwait(orderControllerEventBus::create)
    router.get("/v2/orders").handlerAwait(orderControllerEventBus::list)
    router.get("/v2/orders/:id").handlerAwait(orderControllerEventBus::read)
    router.put("/v2/orders").handlerAwait(orderControllerEventBus::update)
    router.delete("/v2/orders/:id").handlerAwait(orderControllerEventBus::delete)
  }

}
