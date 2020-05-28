package com.daylight.mooddetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.addeditmood.AddEditMoodActivity
import com.daylight.addeditmood.AddEditMoodFragment
import com.daylight.data.moods.MoodTracking
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


/**
 * Main UI for the mood detail screen.
 */
class MoodDetailFragment : Fragment(), MoodDetailContract.View {

    private lateinit var detailTitle: TextView
    private lateinit var moodTrackingList: ListView

    override lateinit var presenter: MoodDetailContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.mooddetail_frag, container, false)
        setHasOptionsMenu(true)
        with(root) {
            detailTitle = findViewById(R.id.mood_detail_title)
            moodTrackingList = findViewById(R.id.mood_tracking_list)
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_mood)?.setOnClickListener {
            presenter.editMood()
        }

        moodTrackingList.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { adapter, v, position, p3 ->
                val trackingDate = adapter!!.getItemAtPosition(position)

                val cal = Calendar.getInstance()
                val sdf =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
                cal.time = sdf.parse(trackingDate as String)
                cal.set(Calendar.AM_PM, 1)

                presenter.deleteMoodTracking(cal)

                fragmentManager?.beginTransaction()?.detach(this@MoodDetailFragment)?.attach(this@MoodDetailFragment)?.commit()

                presenter.loadMoodTracking()

                true
            }

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val deletePressed = item.itemId == R.id.menu_delete
        if (deletePressed) presenter.deleteMood()
        return deletePressed
    }

/*    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.mooddetail_fragment_menu, menu)
    }*/

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            detailTitle.text = getString(R.string.loading)
        }
    }

    override fun hideTitle() {
        detailTitle.visibility = View.GONE
    }

    override fun showEditMood(moodId: String) {
        val intent = Intent(context, AddEditMoodActivity::class.java)
        intent.putExtra(AddEditMoodFragment.ARGUMENT_EDIT_MOOD_ID, moodId)
        startActivityForResult(intent, REQUEST_EDIT_MOOD)
    }

    override fun showMoodDeleted() {
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_MOOD) {
            // If the mood was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }

    override fun showTitle(title: String) {
        with(detailTitle) {
            visibility = View.VISIBLE
            text = title
        }
    }

    override fun showMissingMood() {
        detailTitle.text = getString(R.string.no_data)
    }

    override fun showMoodTracking(moodTracking: List<MoodTracking>) {

        var moodTrackingTimes = moodTracking.map { "${
        it.date.get(Calendar.DAY_OF_MONTH)}/${
        it.date.get(Calendar.MONTH)}/${
        it.date.get(Calendar.YEAR)}" }

        // Display a message if the mood hasn't been tracked
        if (moodTracking.isEmpty()) {
            moodTrackingTimes = listOf("No tracking recorded for this mood!")
        }

        val itemsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, moodTrackingTimes.toMutableList())

        moodTrackingList.adapter = itemsAdapter
    }

    companion object {

        private val ARGUMENT_MOOD_ID = "MOOD_ID"

        private val REQUEST_EDIT_MOOD = 1

        fun newInstance(moodId: String?) =
            MoodDetailFragment().apply {
                arguments = Bundle().apply { putString(ARGUMENT_MOOD_ID, moodId) }
            }
    }

}
