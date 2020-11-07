package com.example.prm02.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Note::class])
abstract class NoteDB: RoomDatabase() {

    abstract fun getDAO():NoteDAO

}