package com.uri.contact.model

import java.io.Serializable

data class Contact(
    val id: Int,
    var name: String,
    var email: String,
    var number: String
) : Serializable