package com.service.example.application.configuration

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.getConfigAwait

object Config {

  private lateinit var retriever: ConfigRetriever
  private const val SCAN_PERIOD: Long = 3000 // 30s
  private const val PORT_NUMBER = 443

  suspend fun load(vertx: Vertx): JsonObject {
    val options = getOptions()
    retriever = ConfigRetriever.create(vertx, options)
    retriever.getConfigAwait()
      return get()
  }

  fun get(): JsonObject {
    return retriever.cachedConfig
  }

  private fun getOptions(): ConfigRetrieverOptions {
    val configRetrieverOptions = ConfigRetrieverOptions()
    configRetrieverOptions.stores = getStores()
    configRetrieverOptions.scanPeriod = SCAN_PERIOD
    return configRetrieverOptions
  }

  private fun getStores(): List<ConfigStoreOptions> {
    val stores = mutableListOf<ConfigStoreOptions>()

    // Default configuration file
    val fileStore = ConfigStoreOptions()
      .setType("file")
      .setConfig(JsonObject().put("path", "application.json"))

    stores.add(fileStore)

      // Consul configuration
      val consulStore = ConfigStoreOptions()
        .setType("consul")
        .setConfig(JsonObject().put("host", "consul.peya.co")
          .put("port", PORT_NUMBER).put("ssl", true)
          .put("prefix", "services/kotlin-example/stg")
        )
        .setOptional(false)

      stores.add(consulStore)

    return stores
  }
}
