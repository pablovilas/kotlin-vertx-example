package com.service.example.models

import java.time.LocalDate

data class User(
    var id: Long,
    var name: String,
    var lastName: String,
    var birthdate: LocalDate
)
