package com.service.example.verticles

import com.service.example.controllers.OrderController
import io.vertx.ext.web.Router

class Server : AbstractServer() {

  private lateinit var orderController : OrderController

  override fun initializeControllers() {
    orderController = OrderController(vertx)
  }

  override fun addRoutes(router: Router) {
    router.post("/v1/orders").handlerAwait(orderController::create)
    router.post("/v1/orders/:id/confirm").handlerAwait(orderController::confirm)
    router.post("/v1/orders/:id/cancel").handlerAwait(orderController::cancel)
    router.get("/v1/orders/:id/tracking").handlerAwait(orderController::track)
  }

}
