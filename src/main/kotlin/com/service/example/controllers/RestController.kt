package com.service.example.controllers

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.endAwait

class HttpCode {
  companion object {
    const val OK = 200
    const val CREATED = 201
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val INTERNAL_SERVER_ERROR = 500
  }
}

private val logger = LoggerFactory.getLogger(RoutingContext::class.java)

suspend fun RoutingContext.success(data: Any) {
  respond(this, HttpCode.OK, data)
}

suspend fun RoutingContext.created(data: Any) {
  respond(this, HttpCode.CREATED, data)
}

suspend fun RoutingContext.badRequest(data: Any = "Bad request") {
  error(this, HttpCode.BAD_REQUEST, data)
}

suspend fun RoutingContext.notFound(data: Any = "Not found") {
  error(this, HttpCode.NOT_FOUND, data)
}

suspend fun RoutingContext.internalServerError(data: Any = "Internal server error") {
  val message = this.failure().message ?: data
  error(this, HttpCode.INTERNAL_SERVER_ERROR, message)
}

private suspend fun error(context: RoutingContext, statusCode: Int, data: Any) {
  val response = JsonObject()
    .put("status", statusCode)
    .put("message", data)
  logger.error(response.toString(), context.failure())
  respond(context, statusCode, response)
}

private suspend fun respond(context: RoutingContext, statusCode: Int, data: Any) {
  val buffer = Json.encodeToBuffer(data)
  context.response()
    .putHeader("Content-Type", "application-json")
    .setStatusCode(statusCode)
    .endAwait(buffer)
}
