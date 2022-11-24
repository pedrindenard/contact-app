package com.pdm.contact.presentation.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.contact.feature.data.repository.ContactRepository
import com.pdm.contact.feature.domain.LocalEvent
import com.pdm.contact.feature.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _uiEventState = MutableStateFlow<LocalEvent>(LocalEvent.Empty)
    val uiEventState: StateFlow<LocalEvent> = _uiEventState

    val contactBackup = arrayListOf<Contact>()

    fun deleteContact(id: Int, position: Int) = viewModelScope.launch {
        repository.deleteContact(id).flowOn(Dispatchers.IO).collectLatest { result ->
            when (result) {
                is LocalEvent.Empty -> _uiEventState.value = LocalEvent.Empty
                is LocalEvent.Failure -> _uiEventState.value = LocalEvent.Failure(result.throwable)
                is LocalEvent.RemoveItem -> _uiEventState.value = LocalEvent.RemoveItem(position)
                is LocalEvent.Success -> _uiEventState.value = LocalEvent.Success(
                    result.data.also {
                        contactBackup.clear()
                        contactBackup.addAll(it)
                    }
                )
            }
        }
    }

    fun getContacts() = viewModelScope.launch {
        repository.getContacts().flowOn(Dispatchers.IO).collectLatest { result ->
            when (result) {
                is LocalEvent.Empty -> _uiEventState.value = LocalEvent.Empty
                is LocalEvent.Failure -> _uiEventState.value = LocalEvent.Failure(result.throwable)
                is LocalEvent.RemoveItem -> _uiEventState.value = LocalEvent.RemoveItem(position = 0)
                is LocalEvent.Success -> _uiEventState.value = LocalEvent.Success(
                    result.data.also {
                        contactBackup.clear()
                        contactBackup.addAll(it)
                    }
                )
            }
        }
    }

    fun updateItemOnBackupContact(contact: Contact) = run loop@ {
        contactBackup.forEachIndexed { index, item ->
            if (item.id == contact.id) {
                contactBackup.removeAt(index)
                contactBackup.add(index, contact)
                return@loop
            }
        }
    }

    fun insertItemOnBackupContact(contact: Contact) {
        contactBackup.add(contact)
    }
}