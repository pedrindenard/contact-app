package com.pdm.contact.feature.domain

import com.pdm.contact.feature.domain.model.Contact

sealed class LocalEvent {

    data class Success(val data: List<Contact>) : LocalEvent()

    data class Failure(val throwable: Throwable) : LocalEvent()

    data class RemoveItem(val position: Int) : LocalEvent()

    object Empty : LocalEvent()
}