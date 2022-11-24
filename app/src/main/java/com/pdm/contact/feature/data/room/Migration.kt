package com.pdm.contact.feature.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE contact ADD COLUMN country TEXT NOT NULL DEFAULT '+55'")
        }
    }
}