package com.uri.contact.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uri.contact.model.ContactEntity

@Database(
    version = 1,
    exportSchema = true,
    entities = [ContactEntity::class]
)
abstract class DatabaseImpl : RoomDatabase() {

    abstract fun dao(): DaoImpl

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
                    ).build()

                    instance = room
                    room
                }
            }
        }
    }
}