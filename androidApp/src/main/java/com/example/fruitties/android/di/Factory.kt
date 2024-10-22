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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fruitties.android.database.AppDatabase
import com.example.fruitties.android.database.CartDataStore
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
            .addMigrations(
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5
            )
            .build()
    }

    fun createCartDataStore(): CartDataStore {
        return CartDataStore {
            app.filesDir.resolve(
                "cart.json",
            ).absolutePath
        }
    }

    fun createApi(): FruittieApi = FruittieNetworkApi(
        client = HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
        },
        apiUrl = "https://yenerm.github.io/frutties/",
    )

    companion object {
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Fruittie ADD COLUMN color TEXT NOT NULL DEFAULT 'unknown'")
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `Veggie` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fruitName` TEXT NOT NULL, `servingSize` TEXT NOT NULL, `calories` TEXT NOT NULL)")
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE `Veggie`")
            }
        }
    }
}

val json = Json { ignoreUnknownKeys = true }
