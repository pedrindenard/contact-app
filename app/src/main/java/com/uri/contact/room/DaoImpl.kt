package com.uri.contact.room

import androidx.room.*
import com.uri.contact.model.ContactEntity

@Dao
interface DaoImpl {

    @Query(value = "SELECT * FROM contact")
    fun getAll(): List<ContactEntity>?

    @Update
    suspend fun update(vararg entity: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: ContactEntity)

    @Query(value = "DELETE FROM contact WHERE id = :id")
    suspend fun delete(id: Int)
}