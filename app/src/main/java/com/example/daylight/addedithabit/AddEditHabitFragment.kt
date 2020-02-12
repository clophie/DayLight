package com.example.daylight.addedithabit

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * Main UI for the add habit screen. Users can enter a habit title and description.
 */
class AddEditHabitFragment : Fragment(), AddEditHabitContract.View {

    override lateinit var presenter: AddEditHabitContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var title: TextView
    private lateinit var description: TextView


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveHabit(title.text.toString(), description.text.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addhabit_frag, container, false)
        with(root) {
            title = findViewById(R.id.add_habit_title)
            description = findViewById(R.id.add_habit_description)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun showEmptyHabitError() {
        title.showSnackBar(getString(R.string.empty_habit_message), Snackbar.LENGTH_LONG)
    }

    override fun showHabitsList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }

    companion object {
        const val ARGUMENT_EDIT_HABIT_ID = "EDIT_HABIT_ID"

        fun newInstance(habitId: String?) =
            AddEditHabitFragment().apply {
                arguments = Bundle().apply {
                    putString(AddEditHabitFragment.ARGUMENT_EDIT_HABIT_ID, habitId)
                }
            }
    }
}
