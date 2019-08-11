package com.service.example.clients

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.*
import java.net.URI
import java.util.HashMap
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.*

class SqsClient(
  val vertx: Vertx,
  awsConfig: JsonObject
) : CoroutineScope {

  private val supervisorJob = SupervisorJob()
  override val coroutineContext: CoroutineContext
    get() = vertx.dispatcher() + supervisorJob
  private val sqs: SqsAsyncClient
  private lateinit var queueConfig: JsonObject

  init {
    sqs = createClient(awsConfig)
  }

  fun receive(config: JsonObject, onReceivedMessage: suspend (String) -> Boolean) {
    this.queueConfig = config
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
    return sqs.sendMessage(request.build()).await()
  }

  suspend fun delete(receiptHandle: String) {
    val request = DeleteMessageRequest.builder()
      .queueUrl(queueUrl())
      .receiptHandle(receiptHandle).build()
    sqs.deleteMessage(request).await()
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
    sqs.changeMessageVisibility(request).await()
  }

  fun stop() {
    supervisorJob.cancel()
  }

  private fun getCredentials(config: JsonObject): AwsBasicCredentials {
    val awsJsonObject = config.getJsonObject("aws")
    return AwsBasicCredentials.create(
      awsJsonObject.getString("accessKey"),
      awsJsonObject.getString("secretKey")
    )
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

  private fun createClient(config: JsonObject): SqsAsyncClient {
    val credentials = getCredentials(config)
    val sqsConfig = config.getJsonObject("aws").getJsonObject("sqs")
    val endpointOverride = sqsConfig.getString("endpointOverride")
    val region = sqsConfig.getString("region") ?: Region.US_WEST_1.id()

    val clientBuilder = SqsAsyncClient.builder()
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .region(Region.of(region))
    if (endpointOverride.isNotEmpty()) {
      clientBuilder.endpointOverride(URI(endpointOverride))
    }
    return clientBuilder.build()
  }

  private suspend fun queueUrl(): String {
    var queueUrl = queueConfig.getString("queueUrl") ?: String()
    if (queueUrl.isEmpty()) {
      val queueName = queueConfig.getString("queueName")
      queueUrl = sqs.getQueueUrl(
        GetQueueUrlRequest.builder()
          .queueName(queueName)
          .build()
      ).await().queueUrl()
      queueConfig.put("queueUrl", queueUrl)
    }
    return queueUrl
  }

  private fun launchWorkers(onMessage: suspend (String) -> Boolean) = launch {
    val workersAmount = this@SqsClient.queueConfig.getInteger("workers") ?: 1
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
      val messages = sqs.receiveMessage(receiveRequest).await().messages()
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
