package com.service.example.services

import com.service.example.models.Order
import com.service.example.repositories.OrderRepository
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class OrderServiceImpl(vertx: Vertx) : OrderService {

  private val orderRepository = OrderRepository(vertx)

  override suspend fun list() : List<Order> {
    return orderRepository.list()
  }

  override suspend fun create(order: Order) : Order {
    val id = orderRepository.save(order) ?: "0"
    return orderRepository.read(id.toLong())
  }

  override suspend fun read(id: Long) : Order {
    return orderRepository.read(id)
  }

  override suspend fun update(order: Order) : Order {
    val id = orderRepository.save(order) ?: "0"
    return orderRepository.read(id.toLong())
  }

  override suspend fun delete(id: Long) : Order {
    val order = read(id)
    orderRepository.delete(id)
    return order
  }

  companion object {
    private val logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)
  }

}
