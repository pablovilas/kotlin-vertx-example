package com.service.example.application

import com.service.example.clients.AwsClient
import com.service.example.clients.SqsClient
import io.vertx.kotlin.coroutines.CoroutineVerticle

class EventSource : CoroutineVerticle() {

  private lateinit var awsClient: AwsClient
  private val queueClients = mutableListOf<SqsClient>()

  override suspend fun start() {
    super.start()
  }
}
