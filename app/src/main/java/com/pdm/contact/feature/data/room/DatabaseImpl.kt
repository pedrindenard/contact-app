package com.pdm.contact.feature.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pdm.contact.feature.domain.model.ContactEntity

@Database(
    version = 2,
    exportSchema = true,
    entities = [ContactEntity::class]
)
abstract class DatabaseImpl : RoomDatabase(), com.pdm.contact.feature.data.room.Database {

    abstract override fun dao(): DaoImpl

    companion object {

        @Volatile
        private lateinit var instance: DatabaseImpl

        private const val database = "contact.db"

        fun getInstance(context: Context): DatabaseImpl {
            return if (Companion::instance.isInitialized) instance else {
                synchronized(lock = this) {
                    val room = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseImpl::class.java,
                        database
                    ).addMigrations(
                        Migration.MIGRATION_1_2
                    ).build()

                    instance = room
                    room
                }
            }
        }
    }
}