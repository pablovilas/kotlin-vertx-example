package com.service.example.application

import com.service.example.controllers.badRequest
import com.service.example.controllers.internalServerError
import com.service.example.controllers.notFound
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.ext.web.handler.TimeoutHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

abstract class Server : CoroutineVerticle() {

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
    val port = this.config.getJsonObject("server").getInteger("port")
    this.server.requestHandler(this.router)
    this.server.listenAwait(port)
  }

  private fun addRoutes() {
    handleGlobalDefaults()
    handleErrors()
    handleRoutes(this.router)
  }

  private fun handleGlobalDefaults() {
    val catchAllRoute = "/*"
    val jsonMimeType = "application/json"
    this.router.post(catchAllRoute).handler(BodyHandler.create())
    this.router.route(catchAllRoute).handler(TimeoutHandler.create(500))
    this.router.route(catchAllRoute)
      .consumes(jsonMimeType)
      .produces(jsonMimeType)
      .handler(ResponseContentTypeHandler.create())
  }

  private fun handleErrors() {
    this.router.errorHandler(400) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.badRequest()
      }
    }
    this.router.errorHandler(404) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.notFound()
      }
    }
    this.router.errorHandler(500) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.internalServerError()
      }
    }
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
  abstract fun handleRoutes(router: Router)
}
