package com.example.appmobile.data.entities

import androidx.room.*

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    var categoryId: Int = 0,

    @ColumnInfo(name = "category_name")
    val categoryName: String
)
