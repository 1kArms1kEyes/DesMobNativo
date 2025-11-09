package com.example.appmobile.data.entities

import androidx.room.*
import com.example.appmobile.data.entities.Category

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_id")]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    var productId: Int = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    val name: String,
    val price: Double,
    val description: String,
    val size: String,
    val stock: Int,
    val color: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean
)
