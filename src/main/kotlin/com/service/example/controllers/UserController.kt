package com.service.example.controllers

import com.service.example.models.User
import com.service.example.models.converters.ModelConverter
import com.service.example.services.UserServiceImpl
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

class UserController(val vertx: Vertx) {
  private val userService = UserServiceImpl(vertx)

  suspend fun list(context: RoutingContext) {
    val users = userService.list()
    context.success(users)
  }

  suspend fun create(context: RoutingContext) {
    val data = ModelConverter.fromJson<User>(context.bodyAsJson)
    val user = userService.create(data)
    context.created(user)
  }

  suspend fun read(context: RoutingContext) {
    val id = context.request().getParam("id").toLong()
    val user = userService.read(id)
    context.success(user)
  }

  suspend fun update(context: RoutingContext) {
    val data = ModelConverter.fromJson<User>(context.bodyAsJson)
    val user = userService.update(data)
    context.success(user)
  }

  suspend fun delete(context: RoutingContext) {
    val id = context.request().getParam("id").toLong()
    val user = userService.delete(id)
    context.success(user)
  }
}
