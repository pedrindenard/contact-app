package com.pdm.contact.feature.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pdm.contact.feature.domain.model.ContactEntity

@Dao
interface DaoImpl {

    @Query(value = "SELECT * FROM contact")
    fun getAll(): List<ContactEntity>?

    @Query(value = "UPDATE contact SET name = :name, email = :email, number = :number WHERE id=:id")
    suspend fun update(name: String, email: String, number: String, id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: ContactEntity)

    @Query(value = "DELETE FROM contact WHERE id = :id")
    suspend fun delete(id: Int)
}