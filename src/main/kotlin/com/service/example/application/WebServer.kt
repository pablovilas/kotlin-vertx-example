package com.service.example.application

import com.service.example.controllers.OrderController
import com.service.example.controllers.UserController
import io.vertx.ext.web.Router

class WebServer : Server() {

  private lateinit var orderController: OrderController
  private lateinit var userController: UserController
  private lateinit var orderControllerEventBus: OrderController

  override fun initializeControllers() {
    orderController = OrderController(vertx)
    userController = UserController(vertx)
    orderControllerEventBus = OrderController(vertx, usesEventBus = true)
  }

  override fun handleRoutes(router: Router) {
    // V1: Direct service method call and MongoDb storage
    router.post("/v1/orders").handlerAwait(orderController::create)
    router.get("/v1/orders").handlerAwait(orderController::list)
    router.get("/v1/orders/:id").handlerAwait(orderController::read)
    router.put("/v1/orders").handlerAwait(orderController::update)
    router.delete("/v1/orders/:id").handlerAwait(orderController::delete)
    // V1: Redis storage
    router.post("/v1/users").handlerAwait(userController::create)
    router.get("/v1/users").handlerAwait(userController::list)
    router.get("/v1/users/:id").handlerAwait(userController::read)
    router.put("/v1/users").handlerAwait(userController::update)
    router.delete("/v1/users/:id").handlerAwait(userController::delete)
    // V2: Event bus service method call and MongoDb storage
    router.post("/v2/orders").handlerAwait(orderControllerEventBus::create)
    router.get("/v2/orders").handlerAwait(orderControllerEventBus::list)
    router.get("/v2/orders/:id").handlerAwait(orderControllerEventBus::read)
    router.put("/v2/orders").handlerAwait(orderControllerEventBus::update)
    router.delete("/v2/orders/:id").handlerAwait(orderControllerEventBus::delete)
  }
}
