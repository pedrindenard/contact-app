package com.pdm.contact.feature.domain

import com.pdm.contact.feature.domain.model.Contact

sealed class FormEvent {

    data class InputDataIsValid(val data: Contact) : FormEvent()

    object InputFieldEmailIsInvalid : FormEvent()

    object InputFieldNumberIsInvalid : FormEvent()

    object InputFieldNameIsInvalid : FormEvent()

    object InputSuccessfully : FormEvent()
}