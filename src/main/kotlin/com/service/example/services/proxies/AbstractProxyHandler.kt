package com.service.example.services.proxies

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ProxyHandler
import io.vertx.serviceproxy.ServiceException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class AbstractProxyHandler(
    private val vertx: Vertx,
    topLevel: Boolean,
    private val timeoutSeconds: Long
) : ProxyHandler() {

  private val timerID: Long
  private var lastAccessed: Long = 0
  @JvmOverloads
  constructor(vertx: Vertx, timeoutInSecond: Long = DEFAULT_CONNECTION_TIMEOUT) : this(
    vertx,
    true,
    timeoutInSecond
  )

  init {
    if (timeoutSeconds != -1L && !topLevel) {
      var period = timeoutSeconds * TIMEOUT_MULTIPLIER
      if (period > MAX_PERIOD) {
        period = MAX_PERIOD
      }
      this.timerID = vertx.setPeriodic(period) { this.checkTimedOut(it) }
    } else {
      this.timerID = -1
    }
    accessed()
  }

  private fun checkTimedOut(id: Long) {
    val now = System.nanoTime()
    if (now - lastAccessed > timeoutSeconds * CLOSE_MULTIPLIER) {
      close()
    }
  }

  override fun close() {
    if (timerID != -1L) {
      vertx.cancelTimer(timerID)
    }
    super.close()
  }

  private fun accessed() {
    this.lastAccessed = System.nanoTime()
  }

  override fun handle(msg: Message<JsonObject>) {
    try {
      val json = msg.body()
      val action = msg.headers().get("action") ?: throw IllegalStateException("action not specified")
      accessed()
      GlobalScope.launch(this.vertx.dispatcher()) {
        try {
          val result = handle(action, json)
          msg.reply(result)
        } catch (exception: Exception) {
          msg.reply(ServiceException(ERROR_CODE, exception.message))
          throw exception
        }
      }
    } catch (throwable: Throwable) {
      msg.reply(ServiceException(ERROR_CODE, throwable.message))
      throw throwable
    }
  }

  @Throws(IllegalStateException::class)
  abstract suspend fun handle(action: String, message: JsonObject): Any

  companion object {
    private const val DEFAULT_CONNECTION_TIMEOUT: Long = 5 * 60 // 5 minutes
    private const val TIMEOUT_MULTIPLIER: Long = 1000 / 2
    private const val CLOSE_MULTIPLIER: Long = 1000000000 // 1000000 seconds
    private const val MAX_PERIOD: Long = 10000 // 10 seconds
    private const val ERROR_CODE = 500
  }
}
