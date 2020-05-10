package com.example.daylight.data.source

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.daylight.util.Converters
import java.util.*


/**
 * Model class for habit tracking.
 *
 * @param title       title of the habit
 * @param description description of the habit
 * @param id          id of the habit
 */
@Entity(tableName = "habitTracking")
@TypeConverters(Converters::class)
data class HabitTracking @JvmOverloads constructor(
    @ColumnInfo(name = "completionDateTime") var completionDateTime: Calendar = Calendar.getInstance(),
    @ForeignKey(entity = Habit::class, parentColumns = arrayOf("habitid"), childColumns = arrayOf("habitid"), onDelete = CASCADE) @ColumnInfo(name = "habitid") var id: String = UUID.randomUUID().toString(),
    @PrimaryKey @ColumnInfo(name = "timeStampOfEntry") var timestampOfEntry: Calendar = Calendar.getInstance()
)