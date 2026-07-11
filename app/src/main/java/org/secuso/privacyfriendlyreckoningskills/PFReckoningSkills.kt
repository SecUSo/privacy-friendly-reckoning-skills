package org.secuso.privacyfriendlyreckoningskills

import android.util.Log
import androidx.work.Configuration
import org.secuso.pfacore.application.SQLiteHelperConfig
import org.secuso.pfacore.ui.PFApplication
import org.secuso.privacyfriendlyreckoningskills.activities.MainActivity
import org.secuso.privacyfriendlyreckoningskills.backup.HighscoreBackup
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper

/**
 * Application entry point for Privacy Friendly Reckoning Skills.
 */
class PFReckoningSkills : PFApplication() {

    override val name: String
        get() = getString(R.string.app_name)

    override val database
        get() = SQLiteHelperConfig(baseContext, PFASQLiteHelper.DATABASE_NAME)

    override val data
        get() = PFApplicationData.instance(baseContext).data

    override val mainActivity = MainActivity::class.java

    override val appBackup = listOf(
        HighscoreBackup()
    )

    override fun onCreate() {
        super.onCreate()

        PFApplicationData.instance(this).migrateLegacyFirstLaunchPreference(this)
    }

    override val workManagerConfiguration by lazy {
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}