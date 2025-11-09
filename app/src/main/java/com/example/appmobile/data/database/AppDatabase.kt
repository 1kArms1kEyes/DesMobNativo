package com.example.appmobile.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemDao(): CartItemDao

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
                    .addCallback(roomCallback)
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Callback para insertar datos iniciales
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {

                        // Insertar usuario inicial
                        val userDao = database.userDao()
                        userDao.insertUser(
                            User(
                                username = "paula",
                                mail = "paula@mail.com",
                                password = "123456",
                                phone = "3000000000",
                                address = "Calle 123",
                                city = "Bogotá",
                                neighborhood = "Usme"
                            )
                        )

                        // Insertar categorías iniciales
                        val categoryDao = database.categoryDao()
                        categoryDao.insertCategory(Category(categoryName = "Hoodies"))
                        categoryDao.insertCategory(Category(categoryName = "Camisetas"))
                        categoryDao.insertCategory(Category(categoryName = "Snapbacks"))
                        categoryDao.insertCategory(Category(categoryName = "Tenis"))

                        // Insertar carrito iniciales
                        val cartDao = database.cartDao()
                        cartDao.insertCart(
                            Cart(
                                userId = 1,
                                creationDate = "2025-10-10",
                                totalPrice = 170000.0,
                                paymentMethod = "Daviplata",
                                status = "Pendiente de pago"
                            )
                        )
                        cartDao.insertCart(
                            Cart(
                                userId = 1,
                                creationDate = "2025-08-05",
                                totalPrice = 200000.0,
                                paymentMethod = "Daviplata",
                                status = "Pendiente de pago"
                            )
                        )
                        cartDao.insertCart(
                            Cart(
                                userId = 1,
                                creationDate = "2025-07-12",
                                totalPrice = 199000.0,
                                paymentMethod = "Nequi",
                                status = "Pendiente de pago"
                            )
                        )
                        cartDao.insertCart(
                            Cart(
                                userId = 1,
                                creationDate = "2025-05-20",
                                totalPrice = 549000.0,
                                paymentMethod = "Nequi",
                                status = "Pendiente de pago"
                            )
                        )
                        cartDao.insertCart(
                            Cart(
                                userId = 1,
                                creationDate = "2025-02-08",
                                totalPrice = 130000.0,
                                paymentMethod = "Nequi",
                                status = "Pendiente de pago"
                            )
                        )

                        // Insertar productos iniciales
                        val productDao = database.productDao()
                        productDao.insertProduct(
                            Product(
                                categoryId = 1,
                                name = "Hoodie Legend 2Pac",
                                price = 130000.0,
                                description = "Hoodie Legend 2Pac",
                                size = "M",
                                stock = 15,
                                color = "Negro",
                                imageUrl = "https://ejemplo.com/chaqueta.jpg",
                                isActive = true
                            )
                        )
                        productDao.insertProduct(
                            Product(
                                categoryId = 2,
                                name = "Camiseta Sky Flow",
                                price = 70000.0,
                                description = "Camiseta Sky Flow",
                                size = "L",
                                stock = 9,
                                color = "Azul",
                                imageUrl = "https://ejemplo.com/chaqueta.jpg",
                                isActive = true
                            )
                        )
                        productDao.insertProduct(
                            Product(
                                categoryId = 1,
                                name = "Hooodie Flamas",
                                price = 170000.0,
                                description = "Hooodie Flamas",
                                size = "S",
                                stock = 5,
                                color = "Rojo",
                                imageUrl = "https://ejemplo.com/chaqueta.jpg",
                                isActive = true
                            )
                        )
                        productDao.insertProduct(
                            Product(
                                categoryId = 4,
                                name = "Adidas Originales Superstart",
                                price = 549000.0,
                                description = "Adidas Originales Superstart",
                                size = "S",
                                stock = 5,
                                color = "Blanco",
                                imageUrl = "https://ejemplo.com/chaqueta.jpg",
                                isActive = true
                            )
                        )
                        productDao.insertProduct(
                            Product(
                                categoryId = 4,
                                name = "Puma Suede",
                                price = 199000.0,
                                description = "Puma Suede",
                                size = "S",
                                stock = 8,
                                color = "Negro",
                                imageUrl = "https://ejemplo.com/chaqueta.jpg",
                                isActive = true
                            )
                        )

                        // Insertar itemc carrito iniciales
                        val cartItemDao = database.cartItemDao()
                        cartItemDao.insertCartItem(
                            CartItem(
                                productId = 1,
                                cartId = 1,
                                cartItemQuantity = 2
                            )
                        )
                        cartItemDao.insertCartItem(
                            CartItem(
                                productId = 2,
                                cartId = 2,
                                cartItemQuantity = 3
                            )
                        )
                        cartItemDao.insertCartItem(
                            CartItem(
                                productId = 3,
                                cartId = 3,
                                cartItemQuantity = 10
                            )
                        )
                        cartItemDao.insertCartItem(
                            CartItem(
                                productId = 3,
                                cartId = 3,
                                cartItemQuantity = 10
                            )
                        )
                        cartItemDao.insertCartItem(
                            CartItem(
                                productId = 3,
                                cartId = 3,
                                cartItemQuantity = 10
                            )
                        )
                    }
                }
            }
        }
    }
}
