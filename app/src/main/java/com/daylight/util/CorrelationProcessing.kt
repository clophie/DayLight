package com.daylight.util

import com.daylight.data.MoodAndHabitTracking
import java.util.*

class CorrelationProcessing {
    companion object {
        fun findCorrelations(moodAndHabitTracking: List<MoodAndHabitTracking>) : String {
            val moods : MutableList<Pair<String?, Int?>> = mutableListOf()
            val moodTracking : MutableList<Pair<String?, Calendar>> = mutableListOf()
            val habits : MutableList<Pair<String?, String?>> = mutableListOf()
            val habitTracking : MutableList<Pair<Calendar, String?>> = mutableListOf()

            moodAndHabitTracking.forEach {
                if (it.moodName != null && it.score != null) {
                    moods.add(Pair(it.moodName, it.score))
                } else if (it.name != null) {
                    moodTracking.add(Pair(it.name, it.date))
                } else if (it.title != null && it.habitid != null) {
                    habits.add(Pair(it.title, it.habitid))
                } else if (it.habitidFromTracking != null) {
                    habitTracking.add(Pair(it.completionDateTime, it.habitidFromTracking))
                }
            }

            var totalMoodScore = 0
            val today = Calendar.getInstance()
            today.add(Calendar.HOUR, -24 * 30)

            moodTracking.forEach {
                // Only look at the last 30 days
                if (it.second.timeInMillis > today.timeInMillis) {
                    moods.forEach { mood ->
                        if (mood.first == it.first) {
                            totalMoodScore += mood.second!!
                        }
                    }
                }
            }

            // Average mood score across the time tracked
            if(moodTracking.count() != 0) {

                totalMoodScore /= moodTracking.count()
            }

            var prevBiggestDiff = 0
            var prevBiggestDiffHabitName = ""
            var prevBiggestDiffHabitId = ""
            var habitScore = 0
            var habitMoodOccurrenceCount = 0
            var habitMoodScore = 0
            var habitMoodDifference: Int

            habitTracking.forEach {habitTrack ->
                moodTracking.forEach { moodTrack ->
                    val test1 = moodTrack.second.get(Calendar.DAY_OF_YEAR)
                    val test2 =  habitTrack.first.get(Calendar.DAY_OF_YEAR)
                    val test3 = moodTrack.second.get(Calendar.YEAR)
                    val test4 =habitTrack.first.get(Calendar.YEAR)
                    if (moodTrack.second.get(Calendar.DAY_OF_YEAR) == habitTrack.first.get(Calendar.DAY_OF_YEAR) && moodTrack.second.get(
                            Calendar.YEAR) == habitTrack.first.get(Calendar.YEAR)) {
                        habitMoodOccurrenceCount++
                        moods.forEach {
                            if(it.first == moodTrack.first) {
                                habitScore += it.second!!
                            }
                        }
                    }
                }

                if(habitMoodOccurrenceCount != 0) {
                    habitMoodScore = habitScore / habitMoodOccurrenceCount
                }

                habitMoodDifference = habitMoodScore - totalMoodScore

                if (habitMoodDifference > prevBiggestDiff) {
                    prevBiggestDiff = habitMoodDifference
                    prevBiggestDiffHabitId = habitTrack.second!!
                }

                habits.forEach {
                    if(it.second == prevBiggestDiffHabitId) {
                        prevBiggestDiffHabitName = it.first.toString()
                    }
                }
            }

            return if (prevBiggestDiff >= 0.5) {
                prevBiggestDiffHabitName
            } else {
                ""
            }
        }
    }
}