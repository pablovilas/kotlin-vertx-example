package com.service.example.repositories

import com.service.example.application.configuration.Config
import com.service.example.models.converters.ModelConverter
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.ext.mongo.findAwait
import io.vertx.kotlin.ext.mongo.findOneAwait
import io.vertx.kotlin.ext.mongo.removeDocumentAwait
import io.vertx.kotlin.ext.mongo.saveAwait

abstract class EntityRepository<T, ID>(vertx: Vertx, val collection: String) {

  val client: MongoClient = MongoClient.createShared(vertx, Config.get().getJsonObject("database"))

  suspend inline fun <reified T>list(query: JsonObject = JsonObject()): List<T> {
    val result = client.findAwait(collection, query)
    return ModelConverter.fromJson(result)
  }

  suspend inline fun <reified T>save(entity: T): String? {
    val document = ModelConverter.toJson(entity)
    return client.saveAwait(collection, document)
  }

  suspend inline fun <reified T>read(id: ID, fields: JsonObject = JsonObject()): T {
    val query = JsonObject().put("_id", id)
    val result = client.findOneAwait(collection, query, fields) ?: JsonObject()
    return ModelConverter.fromJson(result)
  }

  suspend fun delete(id: ID) {
    val query = JsonObject().put("_id", id)
    client.removeDocumentAwait(collection, query)
  }
}
