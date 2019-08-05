package com.service.example.controllers

import com.service.example.models.Order
import com.service.example.models.converters.ModelConverter
import com.service.example.services.OrderService
import com.service.example.services.OrderServiceImpl
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

class OrderController(val vertx: Vertx, usesEventBus: Boolean = false) {

  private val orderService = getService(usesEventBus)

  suspend fun list(context: RoutingContext) {
    val orders = orderService.list()
    context.success(orders)
  }

  suspend fun create(context: RoutingContext) {
    val data = ModelConverter.fromJson<Order>(context.bodyAsJson)
    val order = orderService.create(data)
    context.created(order)
  }

  suspend fun read(context: RoutingContext) {
    val id = context.request().getParam("id").toLong()
    val order = orderService.read(id)
    context.success(order)
  }

  suspend fun update(context: RoutingContext) {
    val data = ModelConverter.fromJson<Order>(context.bodyAsJson)
    val order = orderService.update(data)
    context.success(order)
  }

  suspend fun delete(context: RoutingContext) {
    val id = context.request().getParam("id").toLong()
    val order = orderService.delete(id)
    context.success(order)
  }

  private fun getService(usesEventBus: Boolean): OrderService {
    if (usesEventBus) {
      return OrderService.createProxy(this.vertx)
    }
    return OrderServiceImpl(this.vertx)
  }

}
