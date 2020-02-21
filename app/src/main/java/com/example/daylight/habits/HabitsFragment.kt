package com.example.daylight.habits

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.daylight.R
import com.example.daylight.addedithabit.AddEditHabitActivity
import com.example.daylight.data.source.Habit
import com.example.daylight.habitdetail.HabitDetailActivity
import com.example.daylight.util.showSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList


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
    private lateinit var filteringLabelView: TextView

    /**
     * Listener for clicks on habits in the ListView.
     */
    internal var itemListener: HabitItemListener = object : HabitItemListener {
        override fun onHabitClick(clickedHabit: Habit) {
            presenter.openHabitDetails(clickedHabit)
        }

        override fun onCompleteHabitClick(completedHabit: Habit) {
            presenter.completeHabit(completedHabit)
        }

        override fun onActivateHabitClick(activatedHabit: Habit) {
            presenter.activateHabit(activatedHabit)
        }
    }

    private val listAdapter = HabitsAdapter(ArrayList(0), itemListener)

    override fun onResume() {
        super.onResume()
        presenter.start()
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
                setOnRefreshListener { presenter.loadHabits(false) }
            }

            filteringLabelView = findViewById(R.id.filteringLabel)
            habitsView = findViewById(R.id.habitsLL)

            // Set up  no habits view
            noHabitsView = findViewById(R.id.noHabits)
            noHabitIcon = findViewById(R.id.noHabitsIcon)
            noHabitMainView = findViewById(R.id.noHabitsMain)
            noHabitAddView = (findViewById<TextView>(R.id.noHabitsAdd)).also {
                it.setOnClickListener { showAddHabit() }
            }
        }

        // Set up floating action button
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_add_habit).apply {
            setImageResource(R.drawable.ic_add)
            setOnClickListener { presenter.addNewHabit() }
        }
        setHasOptionsMenu(true)

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> presenter.loadHabits(true)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.habits_fragment_menu, menu)
    }

    override fun showFilteringPopUpMenu() {
        val activity = activity ?: return
        val context = context ?: return
        PopupMenu(context, activity.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_habits, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.active -> presenter.currentFiltering = HabitsFilterType.ACTIVE_HABITS
                    R.id.completed -> presenter.currentFiltering = HabitsFilterType.COMPLETED_HABITS
                    else -> presenter.currentFiltering = HabitsFilterType.ALL_HABITS
                }
                presenter.loadHabits(false)
                true
            }
            show()
        }
    }

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

    override fun showNoActiveHabits() {
        showNoHabitsViews(resources.getString(R.string.no_habits_active), R.drawable.ic_check_circle_24dp, false)
    }

    override fun showNoHabits() {
        showNoHabitsViews(resources.getString(R.string.no_habits_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    override fun showNoCompletedHabits() {
        showNoHabitsViews(resources.getString(R.string.no_habits_completed), R.drawable.ic_verified_user_24dp, false)
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_habit_message))
    }

    private fun showNoHabitsViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        habitsView.visibility = View.GONE
        noHabitsView.visibility = View.VISIBLE

        noHabitMainView.text = mainText
        noHabitIcon.setImageResource(iconRes)
        noHabitAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun showActiveFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_all)
    }

    override fun showAddHabit() {
        val intent = Intent(context, AddEditHabitActivity::class.java)
        startActivityForResult(intent, AddEditHabitActivity.REQUEST_ADD_HABIT)
    }

    override fun showHabitDetailsUi(habitId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, HabitDetailActivity::class.java).apply {
            putExtra(HabitDetailActivity.EXTRA_HABIT_ID, habitId)
        }
        startActivity(intent)
    }

    override fun showHabitMarkedComplete() {
        showMessage(getString(R.string.habit_marked_complete))
    }

    override fun showHabitMarkedActive() {
        showMessage(getString(R.string.habit_marked_active))
    }

    override fun showCompletedHabitsCleared() {
        showMessage(getString(R.string.completed_habits_cleared))
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

            with(rowView.findViewById<CheckBox>(R.id.complete)) {
                // Active/completed habit UI
                isChecked = habit.isCompleted
                val rowViewBackground =
                    if (habit.isCompleted) R.drawable.list_completed_touch_feedback
                    else R.drawable.touch_feedback
                rowView.setBackgroundResource(rowViewBackground)
                setOnClickListener {
                    if (!habit.isCompleted) {
                        itemListener.onCompleteHabitClick(habit)
                    } else {
                        itemListener.onActivateHabitClick(habit)
                    }
                }
            }
            rowView.setOnClickListener { itemListener.onHabitClick(habit) }
            return rowView
        }
    }

    interface HabitItemListener {

        fun onHabitClick(clickedHabit: Habit)

        fun onCompleteHabitClick(completedHabit: Habit)

        fun onActivateHabitClick(activatedHabit: Habit)
    }

    companion object {

        fun newInstance() = HabitsFragment()
    }

}
