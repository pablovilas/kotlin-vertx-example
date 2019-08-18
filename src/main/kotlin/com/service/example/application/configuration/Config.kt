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

    if (!Environment.isDevelopment()) {

      // Environment configuration file will override default configurations
      val envFileStore = ConfigStoreOptions()
        .setType("file")
        .setConfig(JsonObject().put("path", "application-${Environment.getEnvironment()}.json"))

      // Vault configuration
      val vaultStore = ConfigStoreOptions()
      .setType("vault")

      // Consul configuration
      val consulStore = ConfigStoreOptions()
        .setType("consul")
        .setOptional(true)

      stores.add(envFileStore)
      stores.add(vaultStore)
      stores.add(consulStore)
    }

    return stores
  }
}
