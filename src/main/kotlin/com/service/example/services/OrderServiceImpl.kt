package com.service.example.services

import com.service.example.models.Order
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class OrderServiceImpl(vertx: Vertx) : OrderService {

  //var client: WebClient = WebClient.create(vertx)

  override suspend fun list() : List<Order> {
    //val response = client.post("", "/delivery").sendJsonAwait(order)
    // val json = response.bodyAsJsonObject()
    throw RuntimeException("Shit happens. not proceess")
    return listOf(
      Order(1L, 100L, "Pablo"),
      Order(2L, 100L, "Pablo"), Order(1L, 100L, "Pablo"),
      Order(3L, 100L, "Pablo")
    )
  }

  override suspend fun create(order: Order) : Order {
    //val response = client.post("", "/delivery").sendJsonAwait(order)
   // val json = response.bodyAsJsonObject()
    return Order(1L, 100L, "Pablo")
  }

  override suspend fun read(id: Long) : Order {
    return Order(id, 100L, "Pablo")
  }

  override suspend fun update(order: Order) : Order {
    return order
  }

  override suspend fun delete(id: Long) : Order {
    return Order(1L, 100L, "Pablo")
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
  }

}
