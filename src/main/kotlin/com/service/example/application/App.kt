package com.service.example.application

import io.vertx.core.DeploymentOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.io.InputStreamReader

class App : CoroutineVerticle() {

  override suspend fun start() {
    try {
      vertx.deployVerticleAwait("com.service.example.application.WebServer", DeploymentOptions().setInstances(2))
      vertx.deployVerticleAwait("com.service.example.application.Service", DeploymentOptions().setInstances(2))
    } catch (ex: Exception) {
      logger.error("Cannot start application", ex)
    }
    logger.info(getSplash())
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
    val logger = LoggerFactory.getLogger(App::class.java)
  }

}
