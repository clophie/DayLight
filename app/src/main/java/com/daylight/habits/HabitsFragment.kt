package com.daylight.habits

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.AlarmManagerCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.daylight.R
import com.daylight.addedithabit.AddEditHabitActivity
import com.daylight.addeditmood.AddEditMoodActivity
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.habitdetail.HabitDetailActivity
import com.daylight.trackhabit.TrackHabitActivity
import com.daylight.trackmood.TrackMoodActivity
import com.daylight.util.AppExecutors
import com.daylight.util.ScrollChildSwipeRefreshLayout
import com.daylight.util.notif.AlarmScheduler
import com.daylight.util.showSnackBar
import com.github.clans.fab.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.*


/**
 * Display a grid of [Habit]s. User can choose to view all, active or completed habits.
 */
class HabitsFragment : Fragment(), HabitsContract.View {

    override lateinit var presenter: HabitsContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noHabitsView: View
    private lateinit var noHabitIcon: ImageView
    private lateinit var noHabitMainView: TextView
    private lateinit var noHabitAddView: TextView
    private lateinit var habitsView: LinearLayout

    /**
     * Listener for clicks on habits in the ListView.
     */
    private var itemListener: HabitItemListener = object : HabitItemListener {
        override fun onHabitClick(clickedHabit: Habit) {
            presenter.openHabitDetails(clickedHabit)
        }

        override fun onHabitLongClick(clickedHabit: Habit) : Boolean {
            presenter.confirmDelete(clickedHabit)
            return true
        }
    }

    private val listAdapter = HabitsAdapter(ArrayList(0), itemListener)

    override fun onResume() {
        super.onResume()

        val settings: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        //if (settings!!.getBoolean("first_launch", true)) {
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val timeToAlarm = Calendar.getInstance()
            timeToAlarm.set(Calendar.HOUR_OF_DAY, 1)
            timeToAlarm.set(Calendar.MINUTE,55)
            context?.let { AlarmScheduler.scheduleMoodAlarm(it, alarmManager, timeToAlarm) }

            // record the fact that the app has been started at least once
           // settings.edit().putBoolean("first_launch", false).apply()
       // }

        if (!this::presenter.isInitialized) {
            //Get the database and repo
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            // Create the presenter
            presenter = HabitsPresenter(repo, this)
        }

        presenter.start()

        presenter.loadHabits(true)
        listAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.habits_frag, container, false)

        // Set up habits view
        with(root) {
            val listView = findViewById<ListView>(R.id.habits_list).apply { adapter = listAdapter }

            // Set up progress indicator
            findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout).apply {
                setColorSchemeColors(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorAccent),
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                )
                // Set the scrolling view in the custom SwipeRefreshLayout.
                scrollUpChild = listView
                setOnRefreshListener { presenter.loadHabits(true) }
            }

            habitsView = findViewById(R.id.habitsLL)

            // Set up  no habits view
            noHabitsView = findViewById(R.id.noHabits)
            noHabitIcon = findViewById(R.id.noHabitsIcon)
            noHabitMainView = findViewById(R.id.noHabitsMain)
            noHabitAddView = (findViewById<TextView>(R.id.noHabitsAdd)).also {
                it.setOnClickListener { showAddHabit() }
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
            getString(R.string.habit_notification_channel_id),
            getString(R.string.habit_notification_channel_name)
        )

        createChannel(
            getString(R.string.mood_notification_channel_id),
            getString(R.string.mood_notification_channel_name)
        )

        createChannel(
            getString(R.string.habit_track_notification_channel_id),
            getString(R.string.habit_track_notification_channel_name)
        )

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> presenter.loadHabits(true)
        }
        return true
    }

/*    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.habits_fragment_menu, menu)
    }*/

    override fun setLoadingIndicator(active: Boolean) {
        val root = view ?: return
        with(root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            post { isRefreshing = active }
        }
    }

    override fun showHabits(habits: List<Habit>) {
        listAdapter.habits = habits
        habitsView.visibility = View.VISIBLE
        noHabitsView.visibility = View.GONE
    }

    override fun showNoHabits() {
        showNoHabitsViews(resources.getString(R.string.no_habits_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_habit_message))
    }

    override fun showConfirmDelete(requestedHabit: Habit) {
        AlertDialog.Builder(context)
            .setTitle("Delete Habit")
            .setMessage("Do you really want to delete this habit?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                Toast.makeText(
                    context,
                    "Habit Deleted",
                    Toast.LENGTH_SHORT
                ).show()

                presenter.deleteHabit(requestedHabit)

                fragmentManager?.beginTransaction()?.replace(R.id.contentFrame, HabitsFragment())?.commit()
            }
            .setNegativeButton(android.R.string.no, null).show()
    }

    private fun showNoHabitsViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        habitsView.visibility = View.GONE
        noHabitsView.visibility = View.VISIBLE

        noHabitMainView.text = mainText
        noHabitIcon.setImageResource(iconRes)
        noHabitAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun showAddHabit() {
        val intent = Intent(context, AddEditHabitActivity::class.java)
        startActivityForResult(intent, AddEditHabitActivity.REQUEST_ADD_HABIT)
    }

    override fun showAddMood() {
        val intent = Intent(context, AddEditMoodActivity::class.java)
        startActivityForResult(intent, AddEditMoodActivity.REQUEST_ADD_MOOD)
    }

    override fun showTrackHabit() {
        val intent = Intent(context, TrackHabitActivity::class.java)
        startActivityForResult(intent, TrackHabitActivity.REQUEST_TRACK_HABIT)
    }

    override fun showTrackMood() {
        val intent = Intent(context, TrackMoodActivity::class.java)
        startActivityForResult(intent, TrackMoodActivity.REQUEST_TRACK_MOOD)
    }

    override fun showHabitDetailsUi(habitId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, HabitDetailActivity::class.java).apply {
            putExtra(HabitDetailActivity.EXTRA_HABIT_ID, habitId)
        }
        startActivity(intent)
    }

    override fun showLoadingHabitsError() {
        showMessage(getString(R.string.loading_habits_error))
    }

    private fun showMessage(message: String) {
        view?.showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    private class HabitsAdapter(habits: List<Habit>, private val itemListener: HabitItemListener)
        : BaseAdapter() {

        var habits: List<Habit> = habits
            set(habits) {
                field = habits
                notifyDataSetChanged()
            }

        override fun getCount() = habits.size

        override fun getItem(i: Int) = habits[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val habit = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.habit_item, viewGroup, false)

            with(rowView.findViewById<TextView>(R.id.title)) {
                text = habit.titleForList
            }

            rowView.setOnClickListener { itemListener.onHabitClick(habit) }
            rowView.setOnLongClickListener { itemListener.onHabitLongClick(habit) }
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
            notificationChannel.description = getString(R.string.habit_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    interface HabitItemListener {

        fun onHabitClick(clickedHabit: Habit)

        fun onHabitLongClick(clickedHabit: Habit) : Boolean
    }

    companion object {

        fun newInstance() = HabitsFragment()
    }

}
