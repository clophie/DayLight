package com.example.daylight.util

import androidx.room.TypeConverter
import ca.antonious.materialdaypicker.MaterialDayPicker
import java.util.*


class Converters {
    companion object {

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?): Calendar? {
            val cal = Calendar.getInstance()
            if (value != null) {
                cal.timeInMillis = value
            }
            return cal
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Calendar?): Long? {
            return date?.timeInMillis
        }

        @TypeConverter
        @JvmStatic
        fun toDayList(value: String?): MutableList<MaterialDayPicker.Weekday>? {
            return value?.split(",")?.map { it.trim() }?.map{ when {
                it.contains("monday", true) -> MaterialDayPicker.Weekday.MONDAY
                it.contains("tuesday", true) -> MaterialDayPicker.Weekday.TUESDAY
                it.contains("wednesday", true) -> MaterialDayPicker.Weekday.WEDNESDAY
                it.contains("thursday", true) -> MaterialDayPicker.Weekday.THURSDAY
                it.contains("friday", true) -> MaterialDayPicker.Weekday.FRIDAY
                it.contains("saturday", true) -> MaterialDayPicker.Weekday.SATURDAY
                it.contains("sunday", true) -> MaterialDayPicker.Weekday.SUNDAY
                else -> MaterialDayPicker.Weekday.MONDAY
            } }?.toMutableList()
        }

        @TypeConverter
        @JvmStatic
        fun fromDayList(dayList: MutableList<MaterialDayPicker.Weekday>?): String? {
            return dayList.toString()
        }
    }
}