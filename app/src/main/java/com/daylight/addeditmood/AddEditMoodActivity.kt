package com.daylight.addeditmood

import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daylight.App
import com.daylight.R
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.pack.IconPack


/**
 * Displays an add or edit mood screen.
 */
class AddEditMoodActivity : AppCompatActivity(), IconDialog.Callback {

    private lateinit var addEditMoodPresenter: AddEditMoodPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addmood_act)
        val moodId = intent.getStringExtra(AddEditMoodFragment.ARGUMENT_EDIT_MOOD_ID)


        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(if (moodId == null) R.string.add_mood else R.string.edit_mood)
        }

        val addEditMoodFragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditMoodFragment?
                ?: AddEditMoodFragment.newInstance(moodId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        val shouldLoadDataFromRepo =
        // Prevent the presenter from loading data from the repository if this is a config change.
            // Data might not have loaded when the config change happen, so we saved the state.
            savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true

        // Create the presenter
        val database = DaylightDatabase.getInstance(applicationContext)

        addEditMoodPresenter = AddEditMoodPresenter(moodId,
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()), addEditMoodFragment,
            shouldLoadDataFromRepo)

        // If dialog is already added to fragment manager, get it. If not, create a new instance.
        val iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

        val image: ImageView = findViewById(R.id.moodIcon)
        image.setOnClickListener {
            // Open icon dialog
            iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state so that next time we know if we need to refresh data.
        super.onSaveInstanceState(outState.apply {
            putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, addEditMoodPresenter.isDataMissing)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val iconDialogIconPack: IconPack?
        get() = (application as App).iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<com.maltaisn.icondialog.data.Icon>) {
        // Show a toast with the list of selected icon IDs.
        Toast.makeText(this, "Icons selected: ${icons.map { it.id }}", Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val REQUEST_ADD_MOOD = 1
        private const val ICON_DIALOG_TAG = "icon-dialog"
    }
}