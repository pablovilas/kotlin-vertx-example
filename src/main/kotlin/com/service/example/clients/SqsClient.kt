package com.service.example.clients

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.*
import java.util.HashMap
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.*

class SqsClient(
  private val vertx: Vertx,
  private val sqsClient: SqsAsyncClient,
  private val queueConfig: JsonObject
) : CoroutineScope {

  private val supervisorJob = SupervisorJob()
  override val coroutineContext: CoroutineContext
    get() = vertx.dispatcher() + supervisorJob

  fun receive(onReceivedMessage: suspend (String) -> Boolean) {
    launchWorkers(onReceivedMessage)
  }

  suspend fun send(
    obj: Any,
    attributes: List<MessageAttribute> = emptyList(),
    delaySeconds: Int = 0
  ): SendMessageResponse {
    val messageString = JsonObject.mapFrom(obj).encode()
    return send(messageString, attributes, delaySeconds)
  }

  suspend fun send(
    message: String,
    attributes: List<MessageAttribute> = emptyList(),
    delaySeconds: Int = 0
  ): SendMessageResponse {
    val request = SendMessageRequest
      .builder()
      .queueUrl(queueUrl())
      .messageBody(message)
    if (attributes.isNotEmpty()) {
      request.messageAttributes(buildAttributes(attributes))
    }
    if (delaySeconds > 0) {
      request.delaySeconds(delaySeconds)
    }
    return sqsClient.sendMessage(request.build()).await()
  }

  suspend fun delete(receiptHandle: String) {
    val request = DeleteMessageRequest.builder()
      .queueUrl(queueUrl())
      .receiptHandle(receiptHandle).build()
    sqsClient.deleteMessage(request).await()
  }

  suspend fun delete(message: Message) {
    delete(message.receiptHandle())
  }

  suspend fun changeVisibility(message: Message) {
    val request = ChangeMessageVisibilityRequest.builder()
      .queueUrl(queueUrl())
      .receiptHandle(message.receiptHandle())
      .visibilityTimeout(10)
      .build()
    sqsClient.changeMessageVisibility(request).await()
  }

  fun stop() {
    supervisorJob.cancel()
  }

  private fun buildAttributes(attributes: List<MessageAttribute>): Map<String, MessageAttributeValue> {
    val sqsAttributes = HashMap<String, MessageAttributeValue>()
    for (attribute in attributes) {
      val name = attribute.getName()
      val type = attribute.getType()
      val value = attribute.getValue()
      sqsAttributes[name] = MessageAttributeValue.builder()
        .dataType(type)
        .stringValue(value)
        .build()
    }
    return sqsAttributes
  }

  private suspend fun queueUrl(): String {
    var queueUrl = queueConfig.getString("queueUrl") ?: String()
    if (queueUrl.isEmpty()) {
      val queueName = queueConfig.getString("queueName")
      queueUrl = sqsClient.getQueueUrl(
        GetQueueUrlRequest.builder()
          .queueName(queueName)
          .build()
      ).await().queueUrl()
      queueConfig.put("queueUrl", queueUrl)
    }
    return queueUrl
  }

  private fun launchWorkers(onMessage: suspend (String) -> Boolean) = launch {
    val workersAmount = queueConfig.getInteger("workers") ?: 1
    val messageChannel = Channel<Message>()
    repeat(workersAmount) {
      launchWorker(messageChannel, onMessage)
    }
    launchMessageReceiver(messageChannel)
  }

  private fun CoroutineScope.launchMessageReceiver(channel: SendChannel<Message>) = launch {
    repeat {
      val receiveRequest = ReceiveMessageRequest.builder()
        .queueUrl(queueUrl())
        .waitTimeSeconds(queueConfig.getInteger("waitTimeSeconds") ?: 20)
        .maxNumberOfMessages(queueConfig.getInteger("maxNumberOfMessages") ?: 10)
        .build()
      val messages = sqsClient.receiveMessage(receiveRequest).await().messages()
      messages.forEach {
        channel.send(it)
      }
    }
  }

  private fun CoroutineScope.launchWorker(channel: ReceiveChannel<Message>, onMessage: suspend (String) -> Boolean) =
    launch {
      repeat {
        for (message in channel) {
          try {
            val processed = onMessage.invoke(message.body())
            if (processed) {
              delete(message)
            }
          } catch (ex: Exception) {
            changeVisibility(message)
          }
        }
      }
    }
}

private suspend fun CoroutineScope.repeat(block: suspend () -> Unit) {
  while (isActive) {
    block()
    yield()
  }
}

suspend fun <T> CompletableFuture<T>.await(): T =
  suspendCoroutine { cont: Continuation<T> ->
    whenComplete { result: T, exception: Throwable? ->
      if (exception == null) {
        cont.resume(result)
      } else {
        cont.resumeWithException(exception)
      }
    }
  }
