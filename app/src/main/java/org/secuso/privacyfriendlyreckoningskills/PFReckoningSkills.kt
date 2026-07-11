package org.secuso.privacyfriendlyreckoningskills

import android.util.Log
import androidx.work.Configuration
import org.secuso.pfacore.ui.PFApplication
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager
import org.secuso.privacyfriendlyreckoningskills.activities.MainActivity
import org.secuso.privacyfriendlyreckoningskills.backup.BackupCreator
import org.secuso.privacyfriendlyreckoningskills.backup.BackupRestorer

/**
 * Application entry point for Privacy Friendly Reckoning Skills.
 */
class PFReckoningSkills : PFApplication() {

    override val name: String
        get() = getString(R.string.app_name)

    override val data
        get() = PFApplicationData.instance(baseContext).data

    override val mainActivity = MainActivity::class.java


    override val createBackup = false

    override fun onCreate() {
        super.onCreate()

        PFApplicationData.instance(this).migrateLegacyFirstLaunchPreference(this)

        /*
         * The old backup implementation remains active until the
         * PFA-Core backup migration is completed.
         */
        BackupManager.backupCreator = BackupCreator()
        BackupManager.backupRestorer = BackupRestorer()
    }

    override val workManagerConfiguration by lazy {
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}