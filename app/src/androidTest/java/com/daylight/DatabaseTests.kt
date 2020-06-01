package com.daylight

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitTracking
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.DaylightDatabase
import com.daylight.data.habits.HabitsLocalDataSource
import com.daylight.data.moods.MoodsLocalDataSource
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.AppExecutors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTests {

    private var habitsRepo: HabitsRepository? = null
    private var moodsRepo: MoodsRepository? = null
    private var habitToAdd = Habit("Exercise", "", mutableListOf(MaterialDayPicker.Weekday.MONDAY, MaterialDayPicker.Weekday.WEDNESDAY), Calendar.getInstance())
    private var habitTrackingToAdd = HabitTracking(Calendar.getInstance(), habitToAdd.id)
    private var moodToAdd = Mood(2, "Angry")
    private var moodTrackingToAdd = MoodTracking("Angry", Calendar.getInstance())

    @Before
    fun setup() {
        val database = DaylightDatabase.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        habitsRepo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))
        moodsRepo = MoodsRepository.getInstance(MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))
    }

    @Test
    fun saveHabit() {
        habitsRepo!!.saveHabit(habitToAdd)

        habitsRepo!!.getHabit(habitToAdd.id, object : HabitsDataSource.GetHabitCallback {
            override fun onHabitLoaded(habit: Habit) {
                Assert.assertEquals(habit, habitToAdd)
            }

            override fun onDataNotAvailable() { }
        })
    }

    @Test
    fun insertHabitTracking() {
        habitsRepo!!.insertHabitTracking(habitTrackingToAdd)

        habitsRepo!!.getHabitTrackingByHabitId(habitToAdd.id, object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) {
                Assert.assertEquals(habitTracking[0], habitTrackingToAdd)
            }

            override fun onDataNotAvailable() { }
        })
    }

    @Test
    fun deleteHabitTrackingById() {
        habitsRepo!!.deleteHabitTrackingByHabitId(habitToAdd.id)

        habitsRepo!!.getHabitTrackingByHabitId(habitToAdd.id, object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }

    @Test
    fun deleteHabitTrackingByTimestamp() {
        insertHabitTracking()

        habitsRepo!!.deleteHabitTrackingByTimestamp(habitTrackingToAdd.timestampOfEntry)

        habitsRepo!!.getHabitTrackingByHabitId(habitToAdd.id, object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }
    
    @Test
    fun deleteHabit() {
        habitsRepo!!.deleteHabit(habitToAdd.id)

        habitsRepo!!.getHabitTrackingByHabitId(habitToAdd.id, object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }

    @Test
    fun saveMood() {
        moodsRepo!!.saveMood(moodToAdd)

        moodsRepo!!.getMood(moodToAdd.id, object : MoodsDataSource.GetMoodCallback {
            override fun onMoodLoaded(mood: Mood) {
                Assert.assertEquals(mood, moodToAdd)
            }

            override fun onDataNotAvailable() { }
        })
    }

    @Test
    fun insertMoodTracking() {
        moodsRepo!!.insertMoodTracking(moodTrackingToAdd)

        moodsRepo!!.getMoodTrackingByName(moodToAdd.name, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) {
                Assert.assertEquals(moodTracking[0], moodTrackingToAdd)
            }

            override fun onDataNotAvailable() { }
        })
    }

    @Test
    fun deleteMoodTrackingById() {
        moodsRepo!!.deleteMoodTrackingByName(moodToAdd.name)

        moodsRepo!!.getMoodTrackingByName(moodToAdd.name, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }

    @Test
    fun deleteTrackingByTimestamp() {
        insertMoodTracking()

        moodsRepo!!.deleteMoodTrackingByTimestamp(moodTrackingToAdd.date)

        moodsRepo!!.getMoodTrackingByName(moodToAdd.id, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }

    @Test
    fun deleteMood() {
        moodsRepo!!.deleteMood(moodToAdd.id)

        moodsRepo!!.getMoodTrackingByName(moodToAdd.name, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) { }

            override fun onDataNotAvailable() {
                Assert.assertTrue(true)
            }
        })
    }

}