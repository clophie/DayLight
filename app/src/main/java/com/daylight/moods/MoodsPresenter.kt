package com.daylight.moods

import android.app.Activity
import com.daylight.addeditmood.AddEditMoodActivity
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodsDataSource
import java.util.ArrayList


/**
 * Listens to user actions from the UI ([MoodFragment]), retrieves the data and updates
 * the UI as required.
 * @param moodId ID of the mood to edit or null for a new mood
 *
 * @param moodssRepository a repository of data for moods
 *
 * @param addMoodView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */

class MoodsPresenter(
    val moodsRepository: MoodsDataSource,
    val moodsView: MoodsContract.View
) : MoodsContract.Presenter {

    private var firstLoad = true

    init {
        moodsView.presenter = this
    }

    override fun start() {
        loadMoods(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a mood was successfully added, show snackbar
        if (AddEditMoodActivity.REQUEST_ADD_MOOD ==
            requestCode && Activity.RESULT_OK == resultCode
        ) {
            moodsView.showSuccessfullySavedMessage()
        }
    }

    override fun loadMoods(forceUpdate: Boolean) {
        loadMoods(forceUpdate || firstLoad, false)
        firstLoad = false
    }

    override fun confirmDelete(requestedMood: Mood) {
        moodsView.showConfirmDelete(requestedMood)
    }

    override fun deleteMood(requestedMood: Mood) {
        moodsRepository.deleteMood(requestedMood.id)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [MoodsDataSource]
     * *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadMoods(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            moodsView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            moodsRepository.refreshMoods()
        }

        moodsRepository.getMoods(object : MoodsDataSource.LoadMoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                val moodsToShow = ArrayList<Mood>()

                // We filter the moods based on the requestType
                for (mood in moods) {
                    moodsToShow.add(mood)
                }
                // The view may not be able to handle UI updates anymore
                if (!moodsView.isActive) {
                    return
                }
                moodsView.setLoadingIndicator(false)

                processMoods(moodsToShow)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!moodsView.isActive) {
                    return
                }
                moodsView.showLoadingMoodsError()
            }
        })
    }

    private fun processMoods(moods: List<Mood>) {
        if (moods.isEmpty()) {
            // Show a message indicating there are no moods for that filter type.
            moodsView.showNoMoods()
        } else {
            // Show the list of moods
            moodsView.showMoods(moods)
        }
    }

    override fun addNewMood() {
        moodsView.showAddMood()
    }

    override fun addNewHabit() {
        moodsView.showAddHabit()
    }

    override fun trackMood() {
        moodsView.showTrackMood()
    }

    override fun trackHabit() {
        moodsView.showTrackHabit()
    }

    override fun openMoodDetails(requestedMood: Mood) {
        moodsView.showMoodDetailsUi(requestedMood.id)
    }
}
