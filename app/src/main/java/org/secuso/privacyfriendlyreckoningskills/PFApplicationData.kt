package org.secuso.privacyfriendlyreckoningskills

import android.content.Context
import androidx.lifecycle.map
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.ui.PFData
import org.secuso.pfacore.ui.help.Help
import org.secuso.pfacore.ui.preferences.appPreferences
import org.secuso.pfacore.ui.preferences.settings.appearance
import org.secuso.pfacore.ui.preferences.settings.general
import org.secuso.pfacore.ui.preferences.settings.preferenceFirstTimeLaunch
import org.secuso.pfacore.ui.preferences.settings.settingDeviceInformationOnErrorReport
import org.secuso.pfacore.ui.preferences.settings.settingThemeSelector
import org.secuso.pfacore.ui.tutorial.buildTutorial

/**
 * Provides the application data required by the PFA-Core library.
 *
 * This includes preferences, settings, About information,
 * Help content, and the tutorial.
 */
class PFApplicationData private constructor(context: Context) {

    lateinit var theme: ISettingData<String>
        private set

    lateinit var firstTimeLaunch: Preferable<Boolean>
        private set

    lateinit var includeDeviceDataInReport: Preferable<Boolean>
        private set


    private val preferences = appPreferences(context) {
        preferences {
            firstTimeLaunch = preferenceFirstTimeLaunch
        }

        settings {
            appearance {
                theme = settingThemeSelector
            }

            general {
                includeDeviceDataInReport =
                    settingDeviceInformationOnErrorReport
            }
        }
    }

    private val help = Help.build(context) {
        item {
            title { resource(R.string.help_whatis) }
            description { resource(R.string.help_whatis_answer) }
        }

        item {
            title { resource(R.string.help_game) }
            description { resource(R.string.help_game_answer) }
        }

        item {
            title { resource(R.string.help_pause) }
            description { resource(R.string.help_pause_answer) }
        }

        item {
            title { resource(R.string.help_revisit) }
            description { resource(R.string.help_revisit_answer) }
        }

        item {
            title { resource(R.string.help_score) }
            description { resource(R.string.help_score_answer) }
        }

        item {
            title { resource(R.string.help_privacy) }
            description { resource(R.string.help_privacy_answer) }
        }

        item {
            title { resource(R.string.help_permission) }
            description { resource(R.string.help_permission_answer) }
        }
    }

    private val about = About(
        name = context.getString(R.string.app_name_long),
        version = BuildConfig.VERSION_NAME,
        authors = context.getString(R.string.about_author_names),
        repo = context.getString(R.string.about_github)
    )

    private val tutorial = buildTutorial {
        stage {
            title = context.getString(R.string.slide1_heading)
            images = single(R.mipmap.splash_icon)
            description = context.getString(R.string.slide1_text)
        }

        stage {
            title = context.getString(R.string.slide2_heading)
            images = single(R.mipmap.splash_icon)
            description = context.getString(R.string.slide2_text)
        }

        stage {
            title = context.getString(R.string.slide3_heading)
            images = single(R.mipmap.splash_icon)
            description = context.getString(R.string.slide3_text)
        }
    }

    val data = PFData(
        preferences = preferences,
        about = about,
        help = help,
        tutorial = tutorial,
        theme = theme.state.map { Theme.valueOf(it) },
        firstLaunch = firstTimeLaunch,
        includeDeviceDataInReport = includeDeviceDataInReport
    )

    companion object {
        private var instance: PFApplicationData? = null

        fun instance(context: Context): PFApplicationData {
            if (instance == null) {
                instance = PFApplicationData(context.applicationContext)
            }

            return requireNotNull(instance)
        }
    }
}