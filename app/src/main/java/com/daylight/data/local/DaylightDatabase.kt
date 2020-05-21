package com.daylight.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitTracking
import com.daylight.data.moods.Mood
import com.daylight.data.local.habits.HabitTrackingDao
import com.daylight.data.local.habits.HabitsDao
import com.daylight.data.local.moods.MoodTrackingDao
import com.daylight.data.local.moods.MoodsDao
import com.daylight.data.moods.MoodTracking
import com.daylight.util.Converters


/**
 * The Room Database that contains the Habit table.
 */
@Database(entities = [Habit::class, HabitTracking::class, Mood::class, MoodTracking::class], version = 1)
@TypeConverters(Converters::class)
abstract class DaylightDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitsDao
    abstract fun habitTrackingDao(): HabitTrackingDao
    abstract fun moodDao() : MoodsDao
    abstract fun moodTrackingDao(): MoodTrackingDao

    companion object {

        private var INSTANCE: DaylightDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): DaylightDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        DaylightDatabase::class.java, "Daylight.db")
                        .build()
                }
                return INSTANCE!!
            }
        }
    }

}