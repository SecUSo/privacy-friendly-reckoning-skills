package org.secuso.privacyfriendlyreckoningskills

import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager
import org.secuso.privacyfriendlyreckoningskills.backup.BackupCreator
import org.secuso.privacyfriendlyreckoningskills.backup.BackupRestorer

class PFReckoningSkills : MultiDexApplication(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        BackupManager.backupCreator = BackupCreator()
        BackupManager.backupRestorer = BackupRestorer()
    }

    override val workManagerConfiguration by lazy  {
        Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }
}