package com.pdm.contact.feature.domain.model

import java.io.Serializable

data class Contact(
    val id: Int,
    val name: String,
    val email: String,
    val number: String
) : Serializable {

    val entity: ContactEntity
        get() = ContactEntity(
            id = id,
            name = name,
            email = email,
            number = number
        )
}