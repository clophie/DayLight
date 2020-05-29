package com.daylight.data

import androidx.room.Embedded
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import java.util.*

class MoodAndTracking {
    var score : Int = 0

    var moodName : String = ""

    var id : String = ""

    var date : Calendar? = null
}