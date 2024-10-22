package com.example.fruitties.android.di

import com.example.fruitties.android.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(
    private val factory: Factory,
) {
    val dataRepository: DataRepository by lazy {
        DataRepository(
            api = factory.createApi(),
            database = factory.createRoomDatabase(),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }
}
