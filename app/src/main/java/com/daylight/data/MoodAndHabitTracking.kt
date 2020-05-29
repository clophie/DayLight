package com.daylight.data

import java.util.*

class MoodAndHabitTracking {
    var moodName : String? = ""

    var score : Int? = 0

    // Name from mood tracking
    var name : String? = ""

    // Mood completion date
    var date : Calendar = Calendar.getInstance()

    // Habit title
    var title : String? = ""

    var habitid : String? = ""

    // Habit completion date time
    var completionDateTime : Calendar = Calendar.getInstance()

    // Habit id from tracking
    var habitidFromTracking : String? = ""
}