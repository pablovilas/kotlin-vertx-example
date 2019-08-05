package com.service.example.application

import com.service.example.application.configuration.Config
import io.vertx.core.DeploymentOptions
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.io.InputStreamReader

class App : CoroutineVerticle() {

  override suspend fun start() {
    try {
      val config = Config.load(vertx)
      vertx.deployVerticleAwait(
        "com.service.example.application.WebServer",
        DeploymentOptions().setConfig(config).setInstances(2)
      )
      vertx.deployVerticleAwait(
        "com.service.example.application.Service",
        DeploymentOptions().setConfig(config).setInstances(2)
      )
      logger.info(getSplash())
    } catch (ex: Exception) {
      logger.error("Cannot start application", ex)
    }
  }

  private fun getSplash() : String {
    val inputStream = javaClass.getResourceAsStream("/splash.txt")
    val reader = InputStreamReader(inputStream)
    val splash = reader.readText()
    inputStream.close()
    reader.close()
    return splash
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(App::class.java)
  }

}
