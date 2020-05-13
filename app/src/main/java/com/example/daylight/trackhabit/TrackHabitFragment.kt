package com.example.daylight.trackhabit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Main UI for the track habit screen.
 */
class TrackHabitFragment : Fragment(), TrackHabitContract.View {

    override lateinit var presenter: TrackHabitContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.trackhabit_frag, container, false)
        setHasOptionsMenu(true)
        with(root) {
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_confirm_track_habit)?.setOnClickListener {
            presenter.submitTracking()
        }

        return root
    }

    companion object {

        fun newInstance() =
            TrackHabitFragment().apply {
            }
    }
}