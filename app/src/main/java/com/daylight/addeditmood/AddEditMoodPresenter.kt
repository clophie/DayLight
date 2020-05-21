package com.daylight.addeditmood

import android.content.Context
import android.graphics.drawable.Drawable
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodsDataSource
import com.google.common.reflect.Reflection.getPackageName


/**
 * Listens to user actions from the UI ([AddEditMoodFragment]), retrieves the data and updates
 * the UI as required.
 * @param moodId ID of the mood to edit or null for a new mood
 *
 * @param moodsRepository a repository of data for moods
 *
 * @param addMoodView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */
class AddEditMoodPresenter(
    private val moodId: String?,
    val moodsRepository: MoodsDataSource,
    val addMoodView: AddEditMoodContract.View,
    override var isDataMissing: Boolean
) : AddEditMoodContract.Presenter, MoodsDataSource.GetMoodCallback {

    init {
        addMoodView.presenter = this
    }

    override fun start() {
        if (moodId != null && isDataMissing) {
            populateMood()
        }
    }

    override fun saveMood(name: String, score: Int, image: Drawable, context: Context) {
        if (moodId == null) {
            createMood(name, score, image, context)
        } else {
            updateMood(name, score, image, context)
        }
    }

    override fun populateMood() {
        if (moodId == null) {
            throw RuntimeException("populateMood() was called but mood is new.")
        }
        moodsRepository.getMood(moodId, this)
    }

    override fun setIcon(icon: Drawable) {
        addMoodView.setIcon(icon.toString())
    }

    override fun onMoodLoaded(mood: Mood) {
        // The view may not be able to handle UI updates anymore
        if (addMoodView.isActive) {
            addMoodView.setName(mood.name)
            addMoodView.setScore(mood.score)
            addMoodView.setIcon(mood.image)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addMoodView.isActive) {
            addMoodView.showEmptyMoodError()
        }
    }

    private fun createMood(name: String, score: Int, image: Drawable, context: Context) {
        val newMood = Mood(
            image.toString(),
            score,
            name
        )
        moodsRepository.saveMood(newMood)
        addMoodView.showMoodsList()
        //AlarmScheduler.scheduleAlarmsForMood(context, newMood)
    }

    private fun updateMood(name: String, score: Int, image: Drawable, context: Context) {
        if (moodId == null) {
            throw RuntimeException("updateMood() was called but mood is new.")
        }
        val mood = Mood(
            image.toString(),
            score,
            name
        )
        moodsRepository.saveMood(mood)
        addMoodView.showMoodsList() // After an edit, go back to the list.
        //AlarmScheduler.updateAlarmsForMood(context, mood)
    }
}
