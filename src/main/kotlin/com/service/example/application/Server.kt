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
    this.router.route(catchAllRoute).handler(TimeoutHandler.create(DEFAULT_TIMEOUT))
    this.router.route(catchAllRoute)
      .consumes(jsonMimeType)
      .produces(jsonMimeType)
      .handler(ResponseContentTypeHandler.create())
  }

  private fun handleErrors() {
    this.router.errorHandler(HttpCode.BAD_REQUEST) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.badRequest()
      }
    }
    this.router.errorHandler(HttpCode.NOT_FOUND) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.notFound()
      }
    }
    this.router.errorHandler(HttpCode.INTERNAL_SERVER_ERROR) { ctx ->
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

  companion object {
    private const val DEFAULT_TIMEOUT: Long = 500 // ms
  }

  abstract fun initializeControllers()
  abstract fun handleRoutes(router: Router)
}

class HttpCode {
  companion object {
    const val OK = 200
    const val CREATED = 201
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val INTERNAL_SERVER_ERROR = 500
  }
}
