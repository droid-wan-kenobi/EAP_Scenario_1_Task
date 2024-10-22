package com.example.fruitties.android

import androidx.lifecycle.LiveData
import com.example.fruitties.android.database.AppDatabase
import com.example.fruitties.android.database.CartData
import com.example.fruitties.android.database.CartItem
import com.example.fruitties.android.database.Fruittie
import com.example.fruitties.android.network.FruittieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DataRepository(
    private val api: FruittieApi,
    private var database: AppDatabase,
    private val scope: CoroutineScope,
) {
    private val cartData: CartData = CartData(database.cartDao())
    fun cartItems(): LiveData<List<CartItem>> {
        scope.launch {
            refreshData()
        }
        return cartData.cart
    }

    suspend fun addToCart(fruittie: Fruittie) {
        cartData.add(fruittie)
    }

    fun getData(): LiveData<List<Fruittie>> {
        scope.launch {
            if (database.fruittieDao().count() < 1) {
                refreshData()
            }
        }
        return loadData()
    }

    fun loadData(): LiveData<List<Fruittie>> {
        return database.fruittieDao().getAllAsLiveData()
    }

    suspend fun refreshData() {
        val response = api.getData()
        database.fruittieDao().insert(response.feed)
    }
}
