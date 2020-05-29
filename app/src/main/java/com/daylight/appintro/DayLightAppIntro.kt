package com.daylight.appintro

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.daylight.R
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

@Suppress("DEPRECATION")
class DayLightAppIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isColorTransitionsEnabled = true

        addSlide(
            AppIntroFragment.newInstance(
            title = "Welcome to DayLight!",
            description = "DayLight is a mood and habit tracking app that will fit in with your routine. Lets take a few moments to see how to get the most out of DayLight!",
            backgroundColor = resources.getColor(R.color.colorPrimary),
            imageDrawable = R.drawable.yellow_sun
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Add Moods and Habits!",
            description = "In the app, you can click on the yellow plus icon to add a new habit or mood to track! If you select weekdays for a habit, then you will be sent notifications on those days to help you track this habit. You'll also be sent a daily notification to track your mood for the day. If you don't want these notifications, head to the settings to turn them off. If you want to delete a mood or habit, simply press and hold the mood/habit you want to delete.",
            backgroundColor = resources.getColor(R.color.colorAccentDark),
            imageDrawable = R.drawable.add_200
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Track Your Moods and Habits!",
            description = "You can tap the same yellow circle in order to track a mood or habit! You can also track these from the notifications mentioned before, however you'll only be able to record one of three moods from the notification, so open up the app in order to track something different! Tap a mood or habit in the list to see what you've tracked, and if you want to remove some tracking just press and hold the entry you want to get rid of!",
            backgroundColor = resources.getColor(R.color.colorPrimary),
            imageDrawable = R.drawable.pencil_200
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Lets Do Some Analysis!",
            description = "Tap on the analysis tab in order to dig into the data you've entered! You'll see your mood over the last 30 days, as well as your most frequently completed habits. You'll also see if there's a particular habit that makes you feel better when you complete it, which you'll also be sent a notification for when one is noticed (again, head to the settings to turn this off).",
            backgroundColor = resources.getColor(R.color.colorAccentDark),
            imageDrawable = R.drawable.add_200
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "So How Do I Properly Form A New Habit?",
            description = "A new habit is most easily formed when planned around an existing trigger event in your routine, for example - I will take my vitamins after I brush my teeth in the morning! You should avoid using the notifcations as reminders to complete your habits, and instead use your pre-existing trigger events. The notifications are simply there to make tracking the habit easier!",
            backgroundColor = resources.getColor(R.color.colorPrimary),
            imageDrawable = R.drawable.question_200
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Habit Formation is Hard!!",
            description = "A common misconception is that it takes 21 days to form a habit. This is completely incorrect! The average amount of time is 66 days, but studies have found that it can be as few as 18 days or as many as 254, depending on the complexity of the habit! You'll also find that it'll be easier to form a habit if your motivation is intrinsic (coming from within!) rather than extrinsic (doing it because of some external reward).",
            backgroundColor = resources.getColor(R.color.colorAccentDark),
            imageDrawable = R.drawable.dead_200
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Let's Get Started!",
            description = "Habit formation may be difficult, but you can do it! Now let's get to tracking! If you want to view this tutorial again, just head to the settings.",
            backgroundColor = resources.getColor(R.color.colorPrimary),
            imageDrawable = R.drawable.happy_200
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}