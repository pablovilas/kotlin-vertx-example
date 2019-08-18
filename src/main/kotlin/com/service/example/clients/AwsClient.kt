package com.service.example.clients

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.net.URI
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

class AwsClient(
    private val vertx: Vertx,
    private val awsConfig: JsonObject
) {

  fun createSqsClient(): SqsAsyncClient {

    val credentials = getCredentials(awsConfig)
    val sqsConfig = awsConfig.getJsonObject("aws").getJsonObject("sqs")
    val endpointOverride = sqsConfig.getString("endpointOverride")
    val region = sqsConfig.getString("region") ?: Region.US_EAST_2.id()

    val clientBuilder = SqsAsyncClient.builder()
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .region(Region.of(region))
    if (endpointOverride.isNotEmpty()) {
      clientBuilder.endpointOverride(URI(endpointOverride))
    }
    return clientBuilder.build()
  }

  private fun getCredentials(config: JsonObject): AwsBasicCredentials {
    val awsJsonObject = config.getJsonObject("aws")
    return AwsBasicCredentials.create(
      awsJsonObject.getString("accessKey"),
      awsJsonObject.getString("secretKey")
    )
  }
}
