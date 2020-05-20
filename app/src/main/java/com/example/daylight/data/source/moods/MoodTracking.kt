package com.example.daylight.data.source.moods

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.daylight.data.source.moods.Mood
import com.example.daylight.util.Converters
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
    @ForeignKey(entity = Mood::class, parentColumns = arrayOf("moodId"), childColumns = arrayOf("moodId"), onDelete = CASCADE) @ColumnInfo(name = "moodId") var moodId: String = UUID.randomUUID().toString(),
    @PrimaryKey @ColumnInfo(name = "date") var date: Calendar = Calendar.getInstance()
)

