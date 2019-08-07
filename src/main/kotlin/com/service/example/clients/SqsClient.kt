package com.service.example.clients

import io.vertx.core.json.JsonObject
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SqsClient(val config: JsonObject) {

  val credentials = AwsBasicCredentials.create(
    config.getString("accessKey"),
    config.getString("accessSecret")
  )
  val client = SqsAsyncClient.builder()
    .region(Region.of(config.getString("region")))
    .credentialsProvider(StaticCredentialsProvider.create(credentials))
    .build()
  //val queueUrl = queueUrl()

  private suspend fun queueUrl(): String {
    var url = this.config.getString("queueUrl")
    if (url == null) {
      try {
        url = client.getQueueUrl(
          GetQueueUrlRequest.builder()
            .queueName(config.getString("queueName"))
            .build()
        ).await().queueUrl()
        //this.config.setUrl(url)
      } catch (ex: QueueDoesNotExistException) {
        ex.printStackTrace()
        //stop()
        /*throw QueueException(
          String.format(
            "Queue %s doesn't exists for region %s. Stopping execution.",
            config.getName(),
            credentials.getRegion()
          ), ex
        )*/
      }

    }
    return url
  }

  suspend fun delete(message: Message): DeleteMessageResponse {
    return delete(message.receiptHandle())
  }

  suspend fun delete(receiptHandle: String): DeleteMessageResponse {
    val request = DeleteMessageRequest.builder()
      .queueUrl(queueUrl())
      .receiptHandle(receiptHandle).build()
    return client.deleteMessage(request).await()
  }

}

private suspend fun <T> CompletableFuture<T>.await(): T =
  suspendCoroutine { cont: Continuation<T> ->
    whenComplete { result: T, exception: Throwable? ->
      if (exception == null) {
        cont.resume(result)
      } else {
        cont.resumeWithException(exception)
      }
    }
  }
