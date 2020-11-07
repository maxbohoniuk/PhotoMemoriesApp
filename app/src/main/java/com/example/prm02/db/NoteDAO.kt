package com.example.prm02.db

import androidx.room.*

@Dao
interface NoteDAO {

    @Insert
    fun insert(vararg notes: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(vararg notes: Note)

    @Query("Select * from Note")
    fun getAllNotes():List<Note>

    //@Query("Select * from Note where id = :id")
    //fun getNoteById(id: Int):Note

    @Query("Select max(id) from Note")
    fun getMaxId():Int

    @Query("Delete from Note")
    fun deleteAll()
}