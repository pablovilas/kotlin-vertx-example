package com.service.example.services

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ProxyHandler
import io.vertx.serviceproxy.ServiceException
import io.vertx.serviceproxy.ServiceExceptionMessageCodec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class ServiceProxyHandler(private val vertx: Vertx, private val address: String) : ProxyHandler() {

  init {
    val eventBus = this.vertx.eventBus()
    this.register(eventBus, this.address)
    eventBus.registerDefaultCodec(
      ServiceException::class.java,
      ServiceExceptionMessageCodec()
    )
  }

  override fun handle(event: Message<JsonObject>) {
    try {
      val json = event.body()
      val action = event.headers().get("action") ?: throw IllegalStateException("action not specified")
      GlobalScope.launch(this.vertx.dispatcher()) {
        try {
          val result = onEvent(action, json)
          event.reply(result)
        } catch (exception: Exception) {
          event.reply(ServiceException(500, exception.message))
          throw exception
        }
      }
    } catch (throwable: Throwable) {
      event.reply(ServiceException(500, throwable.message))
      throw throwable
    }

  }

  abstract suspend fun onEvent(action: String, body: JsonObject) : JsonObject
}
