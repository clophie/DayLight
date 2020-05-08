package com.example.daylight.data.source

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.daylight.util.Converters
import java.util.*
import java.util.Optional.empty

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
    @ColumnInfo(name = "days") var days: MutableList<MaterialDayPicker.Weekday> = mutableListOf<MaterialDayPicker.Weekday>(),
    @ColumnInfo(name = "time") var time: Calendar = Calendar.getInstance(),
    @PrimaryKey @ColumnInfo(name = "habitid") var id: String = UUID.randomUUID().toString()
) {
    /**
     * True if the habit is completed, false if it's active.
     */
    @ColumnInfo(name = "completed") var isCompleted = false

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()
}