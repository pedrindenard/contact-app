package com.pdm.contact.feature.di

import com.pdm.contact.presentation.contact.ContactViewModel
import com.pdm.contact.presentation.form.FormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModel {
    val module = module(override = true) {
        viewModel { ContactViewModel(repository = get()) }
        viewModel { FormViewModel(repository = get()) }
    }
}