package com.daylight.data.moods

import android.graphics.drawable.Drawable
import androidx.room.*
import com.daylight.util.Converters
import java.util.*

/**
 * Model class for a mood.
 *
 * @param name        title of the mood
 * @param id          id of the mood
 * @para score        score of the mood
 */
@Entity(tableName = "moods", indices = [Index(value = ["moodName"], unique = true)])
@TypeConverters(Converters::class)
data class Mood @JvmOverloads constructor(
    @ColumnInfo(name = "score") var score: Int = 0,
    @ColumnInfo(name = "moodName") var name: String = "",
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
) {
    val nameForList: String
        get() = name
}