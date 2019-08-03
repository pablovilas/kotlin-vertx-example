package com.service.example.services.proxies

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.eventbus.sendAwait
import io.vertx.serviceproxy.ServiceException
import io.vertx.serviceproxy.ServiceExceptionMessageCodec

open class AbstractVertxEBProxy @JvmOverloads constructor(
  private val _vertx: Vertx,
  private val _address: String,
  private val _options: DeliveryOptions? = null
) {
  private val closed: Boolean = false

  init {
    try {
      this._vertx.eventBus().registerDefaultCodec(ServiceException::class.java, ServiceExceptionMessageCodec())
    } catch (ex: IllegalStateException) {
    }

  }

  fun send(action: String, obj: Any) {
    val jsonObject = JsonObject.mapFrom(obj)
    send(action, jsonObject)
  }

  fun send(action: String, obj: JsonObject) {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    this._vertx.eventBus().send(this._address, obj, deliveryOptions)
  }

  suspend fun <T> send(action: String, obj: Any): T {
    val jsonObject = JsonObject.mapFrom(obj)
    return send<T>(action, jsonObject)
  }

  suspend fun <T> send(action: String, obj: JsonObject): T {
    try {
      val deliveryOptions = DeliveryOptions().addHeader("action", action)
      return this._vertx.eventBus().sendAwait<T>(this._address, obj, deliveryOptions).body()
    } catch (ex: Exception) {
      ex.printStackTrace() // FIXME
      throw ex
    }
  }
}

