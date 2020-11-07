package com.example.prm02.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Note (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var content:String,
    var photo_path:String,
    var date: String,
    var title: String,
    var longitude: Double,
    var latitude: Double

)