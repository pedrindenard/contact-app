package com.pdm.contact.feature.data.room

interface Database {
    fun dao(): DaoImpl
}