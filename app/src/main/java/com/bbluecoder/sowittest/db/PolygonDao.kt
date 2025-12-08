package com.bbluecoder.sowittest.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.bbluecoder.sowittest.db.entities.PolygonEntity
import com.bbluecoder.sowittest.db.entities.PolygonPointEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PolygonDao {

    @Insert
    suspend fun insertPolygon(polygon: PolygonEntity): Long

    @Insert
    suspend fun insertPoints(points: List<PolygonPointEntity>)

    @Insert
    suspend fun insertPolygonWithPoints(polygon: MPolygon) {
        val polygonId = insertPolygon(polygon.polygon)

        val pointsWithPolygonId = polygon.points.map { it.copy(polygonId = polygonId) }
        insertPoints(pointsWithPolygonId)
    }

    @Transaction
    @Query("SELECT * FROM polygons")
    fun getPolygons() : Flow<List<MPolygon>>

}