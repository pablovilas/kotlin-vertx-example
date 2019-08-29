package com.service.example.services

import com.service.example.models.User
import io.vertx.core.Vertx

class UserServiceImpl(val vertx: Vertx) : UserService {
  //
  private val userRepository = null

  override suspend fun list(): List<User> {
    return emptyList()
  }

  override suspend fun create(user: User): User {
    // userRepository.setAwait(listOf("user:${user.id}", ModelConverter.toJson(user).toString()))
    return user
  }

  override suspend fun read(id: Long): User {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun update(user: User): User {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override suspend fun delete(id: Long): User {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }
}
