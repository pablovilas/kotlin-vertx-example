package com.service.example.application

import com.service.example.clients.SqsClient
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EventSource : CoroutineVerticle() {

  private lateinit var queueClient: SqsClient

  override suspend fun start() {
    super.start()
    queueClient = SqsClient(vertx, config)
    val queuesConfig = this.config.getJsonObject("aws")
      .getJsonObject("sqs")
      .getJsonArray("queues")
    queuesConfig.forEach { config ->
      queueClient.receive(config as JsonObject) { message ->
          val id = message.toInt()
          if (id % 1000 == 0) {
            println("${LocalDateTime.now()} - ${Thread.currentThread()}, Processed from EB: $id")
          }
          return@receive true
      }
    }

    fun senderDemo() = launch {
      var id = 0
      while (true) {
        id++
        queueClient.send(id.toString())
        delay((10L..20L).random())
        if (id % 1000 == 0) {
          println("Messages: $id")
        }
      }
    }
    senderDemo()
  }
}
