package com.service.example.models

data class Order (
  var id: Long = 0,
  var total: Long = 0,
  var user: String? = null
)
