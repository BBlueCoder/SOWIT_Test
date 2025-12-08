package com.bbluecoder.sowittest.db

import androidx.room.Embedded
import androidx.room.Relation
import com.bbluecoder.sowittest.db.entities.PolygonEntity
import com.bbluecoder.sowittest.db.entities.PolygonPointEntity

data class MPolygon(
    @Embedded val polygon: PolygonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "polygonId"
    )
    val points : List<PolygonPointEntity>
)
