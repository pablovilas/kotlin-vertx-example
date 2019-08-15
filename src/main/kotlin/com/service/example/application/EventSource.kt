package com.service.example.application

import com.service.example.clients.AwsClient
import com.service.example.clients.SqsClient
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle

class EventSource : CoroutineVerticle() {

  private lateinit var awsClient: AwsClient
  private val queueClients = mutableListOf<SqsClient>()

  override suspend fun start() {
    super.start()
    awsClient = AwsClient(vertx, config)
    val sqsClient = awsClient.createSqsClient()
    val queuesConfig = this.config.getJsonObject("aws")
      .getJsonObject("sqs")
      .getJsonArray("queues")
    queuesConfig.forEach { config ->
      val queueClient = SqsClient(vertx, sqsClient, config as JsonObject)
      val queueName = config.getString("queueName")
      queueClient.receive { message ->
          println("Received message from $queueName: $message")
          return@receive true
      }
      queueClients.add(queueClient)
    }
  }
}
