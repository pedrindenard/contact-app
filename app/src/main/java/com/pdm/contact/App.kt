package com.pdm.contact

import android.app.Application
import com.pdm.contact.feature.di.Database
import com.pdm.contact.feature.di.Repository
import com.pdm.contact.feature.di.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setKoin()
    }

    private fun setKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)

            val modules = listOf(
                ViewModel.module,
                Repository.module,
                Database.module
            )

            koin.loadModules(modules)
        }
    }
}