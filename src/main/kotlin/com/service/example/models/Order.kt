package com.service.example.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

data class Order(
  @JsonProperty("_id")
  @JsonInclude( Include.NON_NULL )
  var _id: String?,

  @JsonProperty("id")
  var id: Long?,

  @JsonProperty("total")
  var total: Long?,

  @JsonProperty("user")
  var user: String?
)
