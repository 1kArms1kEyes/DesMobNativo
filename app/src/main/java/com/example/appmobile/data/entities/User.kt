package com.example.appmobile.data.entities

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    var userId: Int = 0,

    val username: String,
    val mail: String,
    val password: String,
    val phone: String,
    val address: String,
    val city: String,
    val neighborhood: String
)
