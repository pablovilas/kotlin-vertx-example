package com.service.example.services

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.eventbus.sendAwait

open class EventBusProxy(private val vertx: Vertx, private val address: String) {

  fun send(action: String, obj: Any) {
    val jsonObject = JsonObject.mapFrom(obj)
    send(action, jsonObject)
  }

  fun send(action: String, obj: JsonObject) {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    this.vertx.eventBus().send(this.address, obj, deliveryOptions)
  }

  suspend fun <T>send(action: String, obj: Any) : T {
    return send<T>(action, obj)
  }

  suspend fun <T>send(action: String, obj: JsonObject) : T {
    val deliveryOptions = DeliveryOptions().addHeader("action", action)
    return this.vertx.eventBus().sendAwait<T>(this.address, obj, deliveryOptions).body()
  }

}
