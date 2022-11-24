package com.pdm.contact.feature.di

import com.pdm.contact.feature.data.room.Database
import com.pdm.contact.feature.data.room.DatabaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object Database {
    val module = module {
        single<Database> {
            DatabaseImpl.getInstance(androidContext())
        }
    }
}