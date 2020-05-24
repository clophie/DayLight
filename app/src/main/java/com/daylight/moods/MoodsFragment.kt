package com.daylight.moods

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.daylight.R
import com.daylight.addedithabit.AddEditHabitActivity
import com.daylight.addeditmood.AddEditMoodActivity
import com.daylight.data.habits.Habit
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.mooddetail.MoodDetailActivity
import com.daylight.trackhabit.TrackHabitActivity
import com.daylight.trackmood.TrackMoodActivity
import com.daylight.util.AppExecutors
import com.daylight.util.ScrollChildSwipeRefreshLayout
import com.daylight.util.showSnackBar
import com.github.clans.fab.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

/**
 * Display a grid of [Mood]s. User can choose to view all, active or completed moods.
 */
class MoodsFragment : Fragment(), MoodsContract.View {

    override lateinit var presenter: MoodsContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noMoodsView: View
    private lateinit var noMoodIcon: ImageView
    private lateinit var noMoodMainView: TextView
    private lateinit var noMoodAddView: TextView
    private lateinit var moodsView: LinearLayout

    /**
     * Listener for clicks on moods in the ListView.
     */
    internal var itemListener: MoodItemListener = object : MoodItemListener {
        override fun onMoodClick(clickedMood: Mood) {
            presenter.openMoodDetails(clickedMood)
        }

        override fun onMoodLongClick(clickedMood: Mood) : Boolean {
            presenter.confirmDelete(clickedMood)
            return true
        }
    }

    private val listAdapter = MoodsAdapter(ArrayList(0), itemListener)

    override fun onResume() {
        super.onResume()
        if (!this::presenter.isInitialized) {
            //Get the database and repo
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = MoodsRepository.getInstance(MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

            // Create the presenter
            presenter = MoodsPresenter(repo, this)
        }

        presenter.start()

        presenter.loadMoods(true)
        listAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.moods_frag, container, false)

        // Set up moods view
        with(root) {
            val listView = findViewById<ListView>(R.id.moods_list).apply { adapter = listAdapter }

            // Set up progress indicator
            findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout).apply {
                setColorSchemeColors(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorAccent),
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                )
                // Set the scrolling view in the custom SwipeRefreshLayout.
                scrollUpChild = listView
                setOnRefreshListener { presenter.loadMoods(true) }
            }

            moodsView = findViewById(R.id.moodsLL)

            // Set up  no moods view
            noMoodsView = findViewById(R.id.noMoods)
            noMoodIcon = findViewById(R.id.noMoodsIcon)
            noMoodMainView = findViewById(R.id.noMoodsMain)
            noMoodAddView = (findViewById<TextView>(R.id.noMoodsAdd)).also {
                it.setOnClickListener { showAddMood() }
            }

        }

        // Set up floating action buttons
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_add_habit).apply {
            setOnClickListener { presenter.addNewHabit() }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.fab_add_mood).apply {
            setOnClickListener { presenter.addNewMood() }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.fab_track_habit).apply {
            setOnClickListener { presenter.trackHabit() }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.fab_track_mood).apply {
            setOnClickListener { presenter.trackMood() }
        }

        setHasOptionsMenu(true)

        createChannel(
            getString(R.string.mood_notification_channel_id),
            getString(R.string.mood_notification_channel_name)
        )

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> presenter.loadMoods(true)
        }
        return true
    }

/*    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.moods_fragment_menu, menu)
    }*/

    override fun setLoadingIndicator(active: Boolean) {
        val root = view ?: return
        with(root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            post { isRefreshing = active }
        }
    }

    override fun showMoods(moods: List<Mood>) {
        listAdapter.moods = moods
        moodsView.visibility = View.VISIBLE
        noMoodsView.visibility = View.GONE
    }

    override fun showNoMoods() {
        showNoMoodsViews(resources.getString(R.string.no_moods_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_mood_message))
    }

    override fun showConfirmDelete(requestedMood: Mood) {
        AlertDialog.Builder(context)
            .setTitle("Delete Mood")
            .setMessage("Do you really want to delete this mood?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                Toast.makeText(
                    context,
                    "Mood Deleted",
                    Toast.LENGTH_SHORT
                ).show()

                presenter.deleteMood(requestedMood)

                fragmentManager?.beginTransaction()?.replace(R.id.contentFrame, MoodsFragment())?.commit()
            }
            .setNegativeButton(android.R.string.no, null).show()
    }

    private fun showNoMoodsViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        moodsView.visibility = View.GONE
        noMoodsView.visibility = View.VISIBLE

        noMoodMainView.text = mainText
        noMoodIcon.setImageResource(iconRes)
        noMoodAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun showAddMood() {
        val intent = Intent(context, AddEditMoodActivity::class.java)
        startActivityForResult(intent, AddEditMoodActivity.REQUEST_ADD_MOOD)
    }

    override fun showAddHabit() {
        val intent = Intent(context, AddEditHabitActivity::class.java)
        startActivityForResult(intent, AddEditHabitActivity.REQUEST_ADD_HABIT)
    }

    override fun showTrackMood() {
        val intent = Intent(context, TrackMoodActivity::class.java)
        startActivityForResult(intent, TrackMoodActivity.REQUEST_TRACK_MOOD)
    }

    override fun showTrackHabit() {
        val intent = Intent(context, TrackHabitActivity::class.java)
        startActivityForResult(intent, TrackHabitActivity.REQUEST_TRACK_HABIT)
    }

    override fun showMoodDetailsUi(moodId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, MoodDetailActivity::class.java).apply {
            putExtra(MoodDetailActivity.EXTRA_MOOD_ID, moodId)
        }
        startActivity(intent)
    }

    override fun showLoadingMoodsError() {
        showMessage(getString(R.string.loading_moods_error))
    }

    private fun showMessage(message: String) {
        view?.showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    private class MoodsAdapter(moods: List<Mood>, private val itemListener: MoodItemListener)
        : BaseAdapter() {

        var moods: List<Mood> = moods
            set(moods) {
                field = moods
                notifyDataSetChanged()
            }

        override fun getCount() = moods.size

        override fun getItem(i: Int) = moods[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val mood = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.mood_item, viewGroup, false)

            with(rowView.findViewById<TextView>(R.id.name)) {
                text = mood.nameForList
            }

            with(rowView.findViewById<ImageView>(R.id.moodIcon)) {
                when (mood.score) {
                    1 -> this.setImageResource(R.drawable.ic_mood_1)
                    2 -> this.setImageResource(R.drawable.ic_mood_2)
                    3 -> this.setImageResource(R.drawable.ic_mood_3)
                    4 -> this.setImageResource(R.drawable.ic_mood_4)
                    5 -> this.setImageResource(R.drawable.ic_mood_5)
                }
            }

            rowView.setOnClickListener { itemListener.onMoodClick(mood) }
            rowView.setOnLongClickListener { itemListener.onMoodLongClick(mood) }

            return rowView
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.CYAN
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.mood_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    interface MoodItemListener {

        fun onMoodClick(clickedMood: Mood)

        fun onMoodLongClick(clickedMood: Mood) : Boolean
    }

    companion object {

        fun newInstance() = MoodsFragment()
    }

}