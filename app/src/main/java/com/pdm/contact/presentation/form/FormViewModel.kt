package com.pdm.contact.presentation.form

import android.telephony.PhoneNumberUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.contact.feature.data.repository.ContactRepository
import com.pdm.contact.feature.domain.FormEvent
import com.pdm.contact.feature.domain.LocalEvent
import com.pdm.contact.feature.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FormViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _uiEventState = MutableSharedFlow<FormEvent>()
    val uiEventState: SharedFlow<FormEvent> = _uiEventState

    fun doContact(contact: Contact) = viewModelScope.launch {
        var anythingIsInvalid = false

        if (
            contact.name.isEmpty() ||
            contact.name.isBlank() ||
            contact.name.length < 3
        ) {
            _uiEventState.emit(FormEvent.InputFieldNameIsInvalid)
            anythingIsInvalid = true
        }

        if (
            contact.email.isEmpty() ||
            contact.email.isBlank() ||
            !Patterns.EMAIL_ADDRESS.matcher(contact.email).matches()
        ) {
            _uiEventState.emit(FormEvent.InputFieldEmailIsInvalid)
            anythingIsInvalid = true
        }

        if (
            contact.number.isEmpty() ||
            contact.number.isBlank() ||
            PhoneNumberUtils.isGlobalPhoneNumber(contact.number)
        ) {
            _uiEventState.emit(FormEvent.InputFieldNumberIsInvalid)
            anythingIsInvalid = true
        }

        if (!anythingIsInvalid) {
            _uiEventState.emit(FormEvent.InputDataIsValid(contact))
        }
    }

    fun insertContact(contact: Contact) = viewModelScope.launch {
        repository.addContact(contact).flowOn(Dispatchers.IO).collectLatest { result ->
            if (result is LocalEvent.Success) {
                _uiEventState.emit(FormEvent.InputSuccessfully)
            }
        }
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.updateContact(contact).flowOn(Dispatchers.IO).collectLatest { result ->
            if (result is LocalEvent.Success) {
                _uiEventState.emit(FormEvent.InputSuccessfully)
            }
        }
    }
}