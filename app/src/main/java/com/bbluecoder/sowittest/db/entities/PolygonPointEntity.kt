package com.bbluecoder.sowittest.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "points", foreignKeys = [
    ForeignKey(
        entity = PolygonEntity::class,
        parentColumns = ["id"],
        childColumns = ["polygonId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class PolygonPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val polygonId: Long = 0,
    val lat: Double,
    val lng: Double
)
