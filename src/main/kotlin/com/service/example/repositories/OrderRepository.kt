package com.service.example.repositories

import com.service.example.models.Order
import io.vertx.core.Vertx

class OrderRepository(vertx: Vertx) : EntityRepository<Order, String>(vertx, "orders")
