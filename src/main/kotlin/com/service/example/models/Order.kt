package com.service.example.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Order (
  @JsonProperty("id")
  var id: String?,

  @JsonProperty("total")
  var total: Long?,

  @JsonProperty("user")
  var user: String?
)
