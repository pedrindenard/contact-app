package com.pdm.contact.feature.data.repository

import com.pdm.contact.feature.data.room.Database
import com.pdm.contact.feature.domain.LocalEvent
import com.pdm.contact.feature.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ContactRepositoryImpl(private val database: Database): ContactRepository {

    override fun addContact(contact: Contact): Flow<LocalEvent> = flow {
        database.dao().insert(contact.entity)

        val result = database.dao().getAll() ?: emptyList()
        val map = result.map { it.contact }

        if (result.isEmpty()) emit(LocalEvent.Empty) else emit(LocalEvent.Success(map))
    }.catch {
        emit(LocalEvent.Failure(it))
    }

    override fun updateContact(contact: Contact): Flow<LocalEvent> = flow {
        database.dao().update(contact.name, contact.email, contact.number, contact.id)

        val result = database.dao().getAll() ?: emptyList()
        val map = result.map { it.contact }

        if (result.isEmpty()) emit(LocalEvent.Empty) else emit(LocalEvent.Success(map))
    }.catch {
        emit(LocalEvent.Failure(it))
    }

    override fun deleteContact(id: Int): Flow<LocalEvent> = flow {
        database.dao().delete(id)

        val result = database.dao().getAll() ?: emptyList()
        val map = result.map { it.contact }

        if (result.isEmpty()) emit(LocalEvent.Empty) else emit(LocalEvent.Success(map))
        emit(LocalEvent.RemoveItem(id))
    }.catch {
        emit(LocalEvent.Failure(it))
    }

    override fun getContacts(): Flow<LocalEvent> = flow {
        val result = database.dao().getAll() ?: emptyList()
        val map = result.map { it.contact }

        if (result.isEmpty()) emit(LocalEvent.Empty) else emit(LocalEvent.Success(map))
    }.catch {
        emit(LocalEvent.Failure(it))
    }
}