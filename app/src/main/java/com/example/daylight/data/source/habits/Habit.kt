package com.example.daylight.data.source.habits

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.daylight.util.Converters
import java.util.*

/**
 * Model class for a Habit.
 *
 * @param title       title of the habit
 * @param description description of the habit
 * @param id          id of the habit
 */
@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class Habit @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "days") var days: MutableList<MaterialDayPicker.Weekday> = mutableListOf(),
    @ColumnInfo(name = "time") var time: Calendar = Calendar.getInstance(),
    @PrimaryKey @ColumnInfo(name = "habitid") var id: String = UUID.randomUUID().toString()
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description


    val isEmpty
        get() = title.isEmpty() && description.isEmpty()
}