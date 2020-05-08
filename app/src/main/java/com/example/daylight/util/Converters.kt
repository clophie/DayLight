package com.example.daylight.util

import androidx.room.TypeConverter
import ca.antonious.materialdaypicker.MaterialDayPicker

import java.util.Date


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

/*
    @TypeConverter
    fun toDayList(value: String?): List<MaterialDayPicker.Weekday>? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun fromDayList(dayList: List<MaterialDayPicker.Weekday>): String? {
        return dayList.toString()
    }
*/

}