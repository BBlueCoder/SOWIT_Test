package com.bbluecoder.sowittest.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bbluecoder.sowittest.db.entities.PolygonEntity
import com.bbluecoder.sowittest.db.entities.PolygonPointEntity

@Database(entities = [PolygonEntity::class, PolygonPointEntity::class], version = 1)
@TypeConverters(ColorsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun polygonDao(): PolygonDao
}