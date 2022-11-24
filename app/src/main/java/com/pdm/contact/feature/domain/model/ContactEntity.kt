package com.pdm.contact.feature.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "number") val number: String
) {
    val contact: Contact
        get() = Contact(
            id = id,
            name = name,
            email = email,
            country = country,
            number = number,
        )
}