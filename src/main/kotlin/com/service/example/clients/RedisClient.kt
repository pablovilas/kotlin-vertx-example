package com.service.example.clients

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.SocketAddress
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.redis.client.connectAwait
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

class RedisClient(
    val vertx: Vertx,
    val config: JsonObject
) : CoroutineScope {

  override val coroutineContext: CoroutineContext by lazy { vertx.dispatcher() }
  lateinit var connection: Redis
  lateinit var client: RedisAPI

  suspend fun createClient(): RedisAPI {
    val config = getConfig()
    connection = Redis.createClient(vertx, config).connectAwait()
    connection.exceptionHandler { ex ->
      logger.fatal("Fatal exception connecting with Redis", ex)
    }
    client = RedisAPI.api(connection)
    return client
  }

  private fun getConfig(): RedisOptions {
    val port = config.getInteger("port")
    val host = config.getString("host")
    val database = config.getInteger("database")
    return RedisOptions()
      .setEndpoint(SocketAddress.inetSocketAddress(port, host))
      .setSelect(database)
  }

  companion object {
    private val logger = LoggerFactory.getLogger(RedisClient::class.java)
  }
}
