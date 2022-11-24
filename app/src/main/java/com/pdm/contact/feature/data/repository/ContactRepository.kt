package com.pdm.contact.feature.data.repository

import com.pdm.contact.feature.domain.LocalEvent
import com.pdm.contact.feature.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun addContact(contact: Contact): Flow<LocalEvent>

    fun updateContact(contact: Contact): Flow<LocalEvent>

    fun deleteContact(id: Int): Flow<LocalEvent>

    fun getContacts(): Flow<LocalEvent>
}