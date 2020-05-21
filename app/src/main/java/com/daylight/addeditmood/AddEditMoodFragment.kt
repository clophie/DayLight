package com.daylight.addeditmood

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.daylight.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader


/**
 * Main UI for the add mood screen. Users can enter a mood title, description, target day and target time.
 */
class AddEditMoodFragment : Fragment(), AddEditMoodContract.View {

    override lateinit var presenter: AddEditMoodContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var name: TextView
    private lateinit var score: NumberPicker
    private lateinit var image: ImageView


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_mood_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveMood(name.text.toString(), score.value, image.drawable, context)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addmood_frag, container, false)
        with(root) {
            name = findViewById(R.id.add_mood_title)
            score = findViewById(R.id.moodScorePicker)
            image = findViewById(R.id.moodIcon)
        }
        setHasOptionsMenu(true)

        score.minValue = 1
        score.maxValue = 5

        // If dialog is already added to fragment manager, get it. If not, create a new instance.
        val iconDialog = fragmentManager?.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

        image.setOnClickListener {
            // Open icon dialog
            fragmentManager?.let { it1 -> iconDialog.show(it1, ICON_DIALOG_TAG) }
        }

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

    override fun setIcon(icon: String) {
        context?.resources?.getIdentifier(icon, "drawable", context!!.packageName)?.let {
            this.image.setImageResource(
                it
            )
        }
    }

    override fun setIcon(icon: Drawable) {
        this.image.setImageDrawable(icon)
    }

    companion object {
        const val ARGUMENT_EDIT_MOOD_ID = "EDIT_MOOD_ID"
        private const val ICON_DIALOG_TAG = "icon-dialog"

        fun newInstance(moodId: String?) =
            AddEditMoodFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_EDIT_MOOD_ID, moodId)
                }
            }
    }
}
