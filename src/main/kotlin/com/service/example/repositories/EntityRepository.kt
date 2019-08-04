package com.service.example.repositories

interface EntityRepository<T, ID> {
  fun list(): List<T>
  fun save(entity: T): T
  fun read(id: ID): T
  fun delete(id: ID)
}
