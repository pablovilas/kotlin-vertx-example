package com.service.example.verticles

import io.vertx.core.DeploymentOptions
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {
    vertx.deployVerticleAwait("com.service.example.verticles.Server", DeploymentOptions().setInstances(2))
    vertx.deployVerticleAwait("com.service.example.verticles.ServiceVerticle", DeploymentOptions().setInstances(2))
  }

}
