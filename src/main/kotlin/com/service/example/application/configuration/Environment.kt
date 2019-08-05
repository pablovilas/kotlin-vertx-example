package com.service.example.application.configuration

object Environment {

  private val DEVELOPMENT = listOf("development", "dev")
  private val STAGING = listOf("staging", "stg")
  private val PRODUCTION = listOf("production", "prod")

  fun isDevelopment() : Boolean {
    return DEVELOPMENT.contains(getEnvironment())
  }

  fun isStaging() : Boolean {
    return STAGING.contains(getEnvironment())
  }

  fun isProduction() : Boolean {
    return PRODUCTION.contains(getEnvironment())
  }

  fun getEnvironment() : String {
    return System.getProperty("env") ?: "development"
  }

}
