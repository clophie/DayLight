package com.daylight.addeditmood

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daylight.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * Main UI for the add mood screen. Users can enter a mood title, description, target day and target time.
 */
class AddEditMoodFragment : Fragment(), AddEditMoodContract.View {

    override lateinit var presenter: AddEditMoodContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var name: TextView
    private lateinit var score: NumberPicker


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_mood_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveMood(name.text.toString(), score.value)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addmood_frag, container, false)
        with(root) {
            name = findViewById(R.id.add_mood_title)
            score = findViewById(R.id.moodScorePicker)
        }
        setHasOptionsMenu(true)

        score.minValue = 1
        score.maxValue = 5

        return root
    }

    override fun showEmptyMoodError() {
    }

    override fun showMoodsList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setName(name: String) {
        this.name.text = name
    }

    override fun setScore(score: Int) {
        this.score.value = score
    }

    companion object {
        const val ARGUMENT_EDIT_MOOD_ID = "EDIT_MOOD_ID"

        fun newInstance(moodId: String?) =
            AddEditMoodFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_EDIT_MOOD_ID, moodId)
                }
            }
    }
}
