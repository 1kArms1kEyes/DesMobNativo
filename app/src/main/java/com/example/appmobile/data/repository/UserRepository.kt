package com.example.appmobile.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.dao.UserDao
import com.example.appmobile.data.entities.User

class UserRepository(private val dao: UserDao) {

    val allUsers: Flow<List<User>> = dao.getAllUsers()

    suspend fun insert(user: User) {
        dao.insertUser(user)
    }

    suspend fun update(user: User) {
        dao.updateUser(user)
    }

    suspend fun delete(user: User) {
        dao.deleteUser(user)
    }

    suspend fun getById(id: Int): User? {
        return dao.getUserById(id)
    }
}