package org.secuso.privacyfriendlyreckoningskills

import android.content.Context
import android.preference.PreferenceManager
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
import org.secuso.pfacore.ui.preferences.settings.input
import org.secuso.pfacore.ui.preferences.settings.switch

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

    lateinit var directFeedback: ISettingData<Boolean>
        private set

    lateinit var showCorrectAnswer: ISettingData<Boolean>
        private set

    lateinit var defaultPlayerName: ISettingData<String>
        private set

    private val preferences = appPreferences(context) {
        preferences {
            firstTimeLaunch = preferenceFirstTimeLaunch
        }

        settings {
            category(R.string.pref_header_feedback) {
                directFeedback = switch {
                    key = "pref_switch_feedback"

                    title {
                        resource(R.string.pref_switch_feedback)
                    }

                    summary {
                        resource(R.string.pref_switch_feedback_summary)
                    }

                    default = false
                    backup = true
                }

                showCorrectAnswer = switch {
                    key = "pref_switch_answer"

                    title {
                        resource(R.string.pref_switch_answer)
                    }

                    summary {
                        resource(R.string.pref_switch_answer_summary)
                    }

                    default = false
                    backup = true

                    dependency = {
                        "pref_switch_feedback" on true
                    }
                }
            }

            category(R.string.pref_header_user) {
                defaultPlayerName = input<String> {
                    key = "weight"

                    title {
                        resource(R.string.pref_text_defaultname)
                    }

                    summary {
                        transform { _, value ->
                            value.ifBlank {
                                context.getString(
                                    R.string.pref_text_defaultname_summary
                                )
                            }
                        }
                    }

                    default = ""
                    backup = true
                }
            }

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

    fun isDirectFeedbackEnabled(): Boolean {
        return directFeedback.value
    }

    fun isCorrectAnswerEnabled(): Boolean {
        return showCorrectAnswer.value
    }

    fun defaultPlayerNameOrNull(): String? {
        return defaultPlayerName.value
            .trim()
            .takeIf { it.isNotEmpty() }
    }
    /**
     * Migrates the tutorial state used by the previous implementation.
     *
     * This prevents existing users from seeing the tutorial again after
     * upgrading to the PFA-Core version.
     */
    fun migrateLegacyFirstLaunchPreference(context: Context) {
        val legacyPreferences = context.getSharedPreferences(
            LEGACY_TUTORIAL_PREFERENCES,
            Context.MODE_PRIVATE
        )

        val corePreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        /*
         * Do not overwrite the PFA-Core value if it has already been saved.
         */
        if (
            legacyPreferences.contains(LEGACY_FIRST_LAUNCH_KEY) &&
            !corePreferences.contains(firstTimeLaunch.key)
        ) {
            firstTimeLaunch.value = legacyPreferences.getBoolean(
                LEGACY_FIRST_LAUNCH_KEY,
                true
            )
        }
    }
    companion object {
        private const val LEGACY_TUTORIAL_PREFERENCES =
            "androidhive-welcome"

        private const val LEGACY_FIRST_LAUNCH_KEY =
            "IsFirstTimeLaunch"

        private var instance: PFApplicationData? = null

        @JvmStatic
        fun instance(context: Context): PFApplicationData {
            if (instance == null) {
                instance = PFApplicationData(context.applicationContext)
            }

            return requireNotNull(instance)
        }
    }
}