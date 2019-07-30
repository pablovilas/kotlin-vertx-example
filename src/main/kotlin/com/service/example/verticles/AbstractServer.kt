package com.service.example.verticles

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

abstract class AbstractServer : CoroutineVerticle() {

  private val server = vertx.createHttpServer()
  private val router = Router.router(vertx)

  override suspend fun start() {
    initializeControllers()
    addRoutes()
    listen()
  }

  private suspend fun listen() {
    server.requestHandler(router)
    server.listenAwait(8080) //TODO: Add port configuration
  }

  private fun addRoutes() {
    addRoutes(router)
  }

  fun Route.handlerAwait(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
      launch(ctx.vertx().dispatcher()) {
        try {
          fn(ctx)
        } catch (e: Exception) {
          ctx.fail(e)
        }
      }
    }
  }

  abstract fun initializeControllers()
  abstract fun addRoutes(router: Router)

}
