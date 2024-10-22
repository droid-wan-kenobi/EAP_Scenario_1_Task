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
package com.example.fruitties.android.di

import android.app.Application
import androidx.room.Room
import com.example.fruitties.android.database.ALL_MIGRATIONS
import com.example.fruitties.android.database.AppDatabase
import com.example.fruitties.android.network.FruittieApi
import com.example.fruitties.android.network.FruittieNetworkApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Factory(private val app: Application) {
    fun createRoomDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context = app,
            klass = AppDatabase::class.java,
            name = "shoppingCart.db"
        )
            .createFromAsset("shoppingCart.db")
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }

    fun createApi(): FruittieApi = FruittieNetworkApi(
        client = HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
        },
        apiUrl = "https://yenerm.github.io/frutties/",
    )
}

val json = Json { ignoreUnknownKeys = true }
