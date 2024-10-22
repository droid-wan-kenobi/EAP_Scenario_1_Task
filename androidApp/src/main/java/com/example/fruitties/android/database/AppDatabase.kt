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
package com.example.fruitties.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Fruittie::class, CartItem::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fruittieDao(): FruittieDao
    abstract fun cartDao(): CartDao
}

internal val MIGRATION_1_2: Migration =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE Veggie")
            db.execSQL("ALTER TABLE Fruittie ADD COLUMN sweetLevel TEXT NOT NULL DEFAULT 'LOW';")
            val updateRowsQuery = """
                UPDATE Fruittie
                SET sweetLevel = CASE
                    WHEN isSweet = '1' THEN 'HIGH'
                    WHEN isSweet = '0' THEN 'LOW'
                    ELSE NULL
                END;
            """.trimIndent()
            db.execSQL(updateRowsQuery)
            val dropColumnQuery = """
                ALTER TABLE Fruittie
                DROP COLUMN isSweet;
            """.trimIndent()
            db.execSQL(dropColumnQuery)
        }
    }

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
