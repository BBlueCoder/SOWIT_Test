package com.bbluecoder.sowittest.db.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "polygons")
data class PolygonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name : String,
    val city : String,
    val color : Color
)
