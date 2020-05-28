package com.daylight.data.moods

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.daylight.util.Converters
import java.util.*

/**
 * Model class for mood tracking.
 *
 * @param title       title of the mood
 * @param id          id of the mood
 */
@Entity(tableName = "moodTracking")
@TypeConverters(Converters::class)
data class MoodTracking @JvmOverloads constructor(
    @ForeignKey(entity = Mood::class, parentColumns = arrayOf("name"), childColumns = arrayOf("name"), onDelete = CASCADE) @ColumnInfo(name = "name") var moodName: String = "",
    @PrimaryKey @ColumnInfo(name = "date") var date: Calendar = Calendar.getInstance()
)

