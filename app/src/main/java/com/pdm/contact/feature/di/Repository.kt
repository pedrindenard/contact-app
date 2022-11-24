package com.pdm.contact.feature.di

import com.pdm.contact.feature.data.repository.ContactRepository
import com.pdm.contact.feature.data.repository.ContactRepositoryImpl
import org.koin.dsl.module

object Repository {
    val module = module(override = true) {
        single<ContactRepository> {
            ContactRepositoryImpl(database = get())
        }
    }
}