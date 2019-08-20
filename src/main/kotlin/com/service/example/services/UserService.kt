package com.service.example.services

import com.service.example.models.User

interface UserService {
  suspend fun list(): List<User>
  suspend fun create(user: User): User
  suspend fun read(id: Long): User
  suspend fun update(user: User): User
  suspend fun delete(id: Long): User
}
