package com.service.example.verticles

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

abstract class AbstractServer : CoroutineVerticle() {

  private lateinit var server: HttpServer
  private lateinit var router: Router

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    this.server = vertx.createHttpServer()
    this.router = Router.router(this.vertx)
  }

  override suspend fun start() {
    initializeControllers()
    addRoutes()
    listen()
  }

  private suspend fun listen() {
    this.server.requestHandler(this.router)
    this.server.listenAwait(8080) //TODO: Add port configuration
  }

  private fun addRoutes() {
    addRoutes(this.router)
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
