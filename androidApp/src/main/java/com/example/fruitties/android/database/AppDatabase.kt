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

import android.annotation.SuppressLint
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.util.useCursor
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Fruittie::class],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.FruittieAutoMigrationSpec::class
        )
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fruittieDao(): FruittieDao

    @RenameColumn.Entries(
        RenameColumn(
            tableName = "Fruittie",
            fromColumnName = "name",
            toColumnName = "fruitName"
        )
    )
    internal class FruittieAutoMigrationSpec : AutoMigrationSpec {
        @SuppressLint("RestrictedApi")
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            db.query(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'room_master_table'"
            ).useCursor {
                check(it.moveToNext())
                check(it.getInt(0) == 1)
            }
        }
    }
}
