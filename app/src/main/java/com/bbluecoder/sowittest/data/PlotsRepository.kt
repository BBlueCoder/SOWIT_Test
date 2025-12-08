package com.bbluecoder.sowittest.data

import androidx.compose.ui.graphics.Color
import com.bbluecoder.sowittest.db.MPolygon
import com.bbluecoder.sowittest.db.PolygonDao
import com.bbluecoder.sowittest.db.entities.PolygonEntity
import com.bbluecoder.sowittest.db.entities.PolygonPointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlotsRepository @Inject constructor(private val dao: PolygonDao) {

    fun getPolygons() : Flow<List<MPolygon>> {
        return dao.getPolygons()
    }

    suspend fun addPolygon(name : String, city : String, color : Color,points : List<LatLng>) {
        val polygon = MPolygon(
            polygon = PolygonEntity(name = name, city = city, color = color),
            points = points.map { PolygonPointEntity(lat = it.latitude, lng = it.longitude) }
        )
        dao.insertPolygonWithPoints(polygon)
    }
}