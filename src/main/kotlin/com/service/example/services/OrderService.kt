package com.service.example.services

import com.service.example.models.Order
import com.service.example.services.proxies.OrderServiceEventBusProxy
import com.service.example.services.proxies.OrderServiceProxyHandler
import io.vertx.core.Vertx

interface OrderService {

  suspend fun list(): List<Order>
  suspend fun create(order: Order): Order
  suspend fun read(id: String): Order
  suspend fun update(order: Order): Order
  suspend fun delete(id: String): Order

  companion object {

    fun createProxy(vertx: Vertx): OrderService {
      return OrderServiceEventBusProxy(vertx, ADDRESS)
    }

    fun createHandler(vertx: Vertx) {
      val orderService = OrderServiceImpl(vertx)
      OrderServiceProxyHandler(vertx, orderService)
        .register(vertx.eventBus(), ADDRESS)
    }

    private const val ADDRESS = "order-service"
  }
}
