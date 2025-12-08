package com.bbluecoder.sowittest.db

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter

class ColorsConverter {

    @TypeConverter
    fun fromColor(color: Color): Long {
        return color.value.toLong()
    }

    @TypeConverter
    fun toColor(value: Long): Color {
        return Color(value.toULong())
    }
}