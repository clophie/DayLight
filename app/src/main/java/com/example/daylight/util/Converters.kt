package com.example.daylight.util

import androidx.room.TypeConverter
import ca.antonious.materialdaypicker.MaterialDayPicker

import java.util.Date


class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?): Date? {
            return if (value == null) null else Date(value)
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Date?): Long? {
            return date?.time
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