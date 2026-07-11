package org.secuso.privacyfriendlyreckoningskills.activities

import android.content.Intent
import org.secuso.pfacore.model.DrawerElement
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.pfacore.ui.activities.DrawerActivity
import org.secuso.privacyfriendlyreckoningskills.R

/**
 * Base activity that provides the shared PFA-Core navigation drawer.
 */
abstract class BaseActivity : DrawerActivity() {

    /**
     * Builds the application-specific and default PFA-Core drawer entries.
     */
    override fun drawer(): DrawerMenu = DrawerMenu.build {
        name = getString(R.string.app_name)
        icon = R.mipmap.icon_launcher

        section {
            activity {
                name = getString(R.string.action_main)
                icon = R.drawable.ic_menu_home
                clazz = MainActivity::class.java
                extras = { intent ->
                    intent.apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                }
            }

            activity {
                name = getString(R.string.action_score)
                icon = R.drawable.ic_menu_info
                clazz = ScoreActivity::class.java
            }
        }

        /*
         * Adds Tutorial, Help, Settings, About and Error Report.
         */
        defaultDrawerSection(this)
    }

    /**
     * Marks the current activity's drawer entry as selected.
     */
    override fun isActiveDrawerElement(
        element: DrawerElement
    ): Boolean {
        return element.name == activeDrawerItemName()
    }

    /**
     * Name of the drawer item belonging to the current activity.
     */
    protected abstract fun activeDrawerItemName(): String
}