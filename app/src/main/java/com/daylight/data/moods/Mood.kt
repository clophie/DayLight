package com.daylight.data.moods

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.daylight.util.Converters
import java.util.*

/**
 * Model class for a mood.
 *
 * @param name        title of the mood
 * @param id          id of the mood
 * @para score        score of the mood
 */
@Entity(tableName = "moods")
@TypeConverters(Converters::class)
data class Mood @JvmOverloads constructor(
    @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "score") var score: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @PrimaryKey @ColumnInfo(name = "moodid") var id: String = UUID.randomUUID().toString()
) {
    val nameForList: String
        get() = name
}