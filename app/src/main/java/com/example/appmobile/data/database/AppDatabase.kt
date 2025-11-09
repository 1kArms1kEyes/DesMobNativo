package com.example.appmobile.data.database

import android.content.Context
import androidx.room.*

import com.example.appmobile.data.entities.*
import com.example.appmobile.data.dao.*

@Database(
    entities = [
        Category::class,
        User::class,
        Product::class,
        Cart::class,
        CartItem::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
