package com.daylight.mooddetail

import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.mooddetail.MoodDetailContract
import com.daylight.mooddetail.MoodDetailFragment
import java.util.*


/**
 * Listens to user actions from the UI ([MoodDetailFragment]), retrieves the data and updates
 * the UI as required.
 */
class MoodDetailPresenter(
    private val moodId: String,
    private val moodName: String,
    private val moodsRepository: MoodsRepository,
    private val moodDetailView: MoodDetailContract.View
) : MoodDetailContract.Presenter {

    init {
        moodDetailView.presenter = this
    }

    override fun start() {
        openMood()
    }

    public fun openMood() {
        if (moodId.isEmpty()) {
            moodDetailView.showMissingMood()
            return
        }

        moodDetailView.setLoadingIndicator(true)
        moodsRepository.getMood(moodId, object : MoodsDataSource.GetMoodCallback {
            override fun onMoodLoaded(mood: Mood) {
                with(moodDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onMoodLoaded
                    }
                    setLoadingIndicator(false)
                }
                showMood(mood)
            }

            override fun onDataNotAvailable() {
                with(moodDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onDataNotAvailable
                    }
                    showMissingMood()
                }
            }
        })

        loadMoodTracking()
    }

    override fun editMood() {
        if (moodId.isEmpty()) {
            moodDetailView.showMissingMood()
            return
        }
        moodDetailView.showEditMood(moodId)
    }

    override fun deleteMood() {
        if (moodId.isEmpty()) {
            moodDetailView.showMissingMood()
            return
        }
        moodsRepository.deleteMood(moodId)
        moodDetailView.showMoodDeleted()
    }

    override fun deleteMoodTracking(date: Calendar) {
        moodsRepository.deleteMoodTrackingByTimestamp(date)
    }

    private fun showMood(mood: Mood) {
        with(moodDetailView) {
            if (moodId.isEmpty()) {
                hideTitle()
            } else {
                showTitle(mood.name)
            }
        }
    }

    override fun loadMoodTracking() {
        moodsRepository.getMoodTrackingByName(moodName, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) {
                with(moodDetailView) {
                    setLoadingIndicator(false)
                }
                showMoodTracking(moodTracking)
            }

            override fun onDataNotAvailable() {
                with(moodDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onDataNotAvailable
                    }
                }
            }

        })
    }

    private fun showMoodTracking(moodTracking: List<MoodTracking>) {
        with(moodDetailView) {
            showMoodTracking(moodTracking)
        }
    }
}