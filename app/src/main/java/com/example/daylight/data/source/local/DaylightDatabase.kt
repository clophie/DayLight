package com.example.daylight.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.daylight.data.source.Habit
import com.example.daylight.util.Converters


/**
 * The Room Database that contains the Habit table.
 */
@Database(entities = [Habit::class], version = 1)
@TypeConverters(Converters::class)
abstract class DaylightDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitsDao

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