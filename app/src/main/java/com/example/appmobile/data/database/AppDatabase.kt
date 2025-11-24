package com.example.appmobile.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking

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
    version = 3
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
                // Build database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                // Assign instance first so it can be reused
                INSTANCE = instance

                // SEED DATA SYNCHRONOUSLY THE FIRST TIME (when users table is empty)
                runBlocking {
                    val userDao = instance.userDao()
                    val userCount = userDao.getUserCount()

                    if (userCount == 0) {
                        // Insertar usuario inicial
                        userDao.insertUser(
                            User(
                                username = "paula",
                                mail = "paula@mail.com",
                                password = "123456",
                                phone = "3000000000",
                                address = "Calle 123",
                                city = "Bogotá",
                                neighborhood = "Usme"
                                // profileImageUri usa el valor por defecto null
                            )
                        )

                        // Insertar categorías iniciales
                        val categoryDao = instance.categoryDao()
                        categoryDao.insertCategory(Category(categoryName = "Hoodies"))
                        categoryDao.insertCategory(Category(categoryName = "Camisetas"))
                        categoryDao.insertCategory(Category(categoryName = "Snapbacks"))
                        categoryDao.insertCategory(Category(categoryName = "Tenis"))

                        // Insertar carritos iniciales
                        val cartDao = instance.cartDao()
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

                        // 👉 You were missing this line:
                        val productDao = instance.productDao()

                        // Producto base: Hoodie Legend 2Pac (M, Negro)
                        productDao.insertProduct(
                            Product(
                                categoryId = 1,
                                name = "Hoodie Legend 2Pac",
                                price = 130000.0,
                                description = "Hoodie Legend 2Pac",
                                size = "M",
                                stock = 15,
                                color = "Negro",
                                imageUrl = "https://drive.google.com/uc?export=download&id=10dKxk93EGHvoE5-PUlM_lG_aiOXvkkQe",
                                isActive = true
                            )
                        )

                        // 🔁 Variante repetida: mismo nombre, otra talla y color
                        productDao.insertProduct(
                            Product(
                                categoryId = 1,
                                name = "Hoodie Legend 2Pac", // MISMO NOMBRE
                                price = 135000.0,
                                description = "Hoodie Legend 2Pac - Talla L Blanca",
                                size = "L",                  // DISTINTA TALLA
                                stock = 7,
                                color = "Blanco",            // DISTINTO COLOR
                                imageUrl = "https://drive.google.com/uc?export=download&id=10dKxk93EGHvoE5-PUlM_lG_aiOXvkkQe",
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
                                imageUrl = "https://drive.google.com/uc?export=download&id=1JLwBgkiL9cHWA_eHcp-X9Th68sQBPdFS",
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
                                imageUrl = "https://drive.google.com/uc?export=download&id=1bmluBnxRXTgw1j7t41R6Zs1uzLHbVb6_",
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
                                imageUrl = "https://drive.google.com/uc?export=download&id=1UpA40hpD3YHh8q85co_2Hfk12rltFElY",
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
                                imageUrl = "https://drive.google.com/uc?export=download&id=126u3kKLMQpWkvOtaMimb5IXTmeCmPCSL",
                                isActive = true
                            )
                        )

                        // Insertar items de carrito iniciales
                        val cartItemDao = instance.cartItemDao()
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
                                productId = 4,
                                cartId = 4,
                                cartItemQuantity = 10
                            )
                        )
                    }
                }

                instance
            }
        }
    }
}
