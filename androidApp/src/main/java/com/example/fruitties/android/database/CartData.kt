package com.example.fruitties.android.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val id: Long,
    val name: String,
    val count: Int,
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): LiveData<List<CartItem>>

    @Query("SELECT * FROM cart_items")
    suspend fun getCartItemsSync(): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(cartItem: CartItem)

    @Delete
    suspend fun deleteItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE count <= 0")
    suspend fun removeZeroCountItems()
}

class CartData(private val cartDao: CartDao) {

    val cart: LiveData<List<CartItem>> = cartDao.getCartItems()

    suspend fun add(fruittie: Fruittie) = update(fruittie, 1)

    suspend fun remove(fruittie: Fruittie) = update(fruittie, -1)

    private suspend fun update(fruittie: Fruittie, diff: Int) {
        withContext(Dispatchers.IO) {
            // Get current items directly from the database
            val currentItems = cartDao.getCartItemsSync()
            val currentItem = currentItems.find { it.id == fruittie.id }
            val newCount = (currentItem?.count ?: 0) + diff

            if (newCount > 0) {
                cartDao.insertItem(CartItem(id = fruittie.id, name = fruittie.name, count = newCount))
            } else {
                currentItem?.let { cartDao.deleteItem(it) }
            }

            // Remove items with zero count
            cartDao.removeZeroCountItems()
        }
    }
}
