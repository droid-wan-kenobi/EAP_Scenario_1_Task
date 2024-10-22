/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fruitties.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.fruitties.android.database.AppDatabase
import com.example.fruitties.android.database.CartDataStore
import com.example.fruitties.android.database.CartItemDetails
import com.example.fruitties.android.database.Fruittie
import com.example.fruitties.android.network.FruittieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataRepository(
    private val api: FruittieApi,
    private var database: AppDatabase,
    private val cartDataStore: CartDataStore,
    private val scope: CoroutineScope,
) {
    fun cartDetails(): LiveData<List<CartItemDetails>> = liveData {
        withContext(Dispatchers.IO) {
            cartDataStore.cart.collect {
                val ids = it.items.map { it.id }
                val fruitties = database.fruittieDao().loadMapped(ids)
                emit(
                    it.items.mapNotNull {
                        fruitties[it.id]?.let { fruittie ->
                            CartItemDetails(fruittie, it.count)
                        }
                    }
                )
            }
        }
    }

    suspend fun addToCart(fruittie: Fruittie) {
        cartDataStore.add(fruittie)
    }

    fun getData(): LiveData<List<Fruittie>> {
        val liveData = MutableLiveData<List<Fruittie>>()
        scope.launch {
            if (database.fruittieDao().count() < 1) {
                refreshData()
            }
            // Switch to the main dispatcher to observe the LiveData
            withContext(Dispatchers.Main) {
                loadData().observeForever { fruitties ->
                    liveData.value = fruitties
                }
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
