package com.example.appmobile.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE user_id = :id")
    suspend fun getUserById(id: Int): User?
}