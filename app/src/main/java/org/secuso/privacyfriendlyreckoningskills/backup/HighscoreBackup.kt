package org.secuso.privacyfriendlyreckoningskills.backup

import android.content.Context
import android.util.JsonReader
import android.util.JsonWriter
import org.secuso.pfacore.application.PFAppBackup
import org.secuso.pfacore.application.PFModelApplication
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil

/**
 * Backs up and restores the separate highscore preferences file.
 *
 * The key and JSON structure are intentionally kept compatible with
 * backups created before the PFA-Core migration.
 */
class HighscoreBackup : PFAppBackup {

    override val key: String = BACKUP_KEY

    override fun backup(writer: JsonWriter): JsonWriter {
        val preferences =
            PFModelApplication.instance.getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE
            )

        writer.name(key)
        PreferenceUtil.writePreferences(writer, preferences)

        return writer
    }

    override fun restore(
        key: String,
        reader: JsonReader,
        context: Context
    ): JsonReader {
        require(key == this.key) {
            "Unsupported backup key: $key"
        }

        val editor = context.getSharedPreferences(
            PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).edit()

        reader.beginObject()

        while (reader.hasNext()) {
            val name = reader.nextName()

            when {
                STRING_KEYS.matches(name) -> {
                    editor.putString(name, reader.nextString())
                }

                INTEGER_KEYS.matches(name) -> {
                    editor.putInt(name, reader.nextInt())
                }

                name == CONTINUE_KEY -> {
                    editor.putBoolean(name, reader.nextBoolean())
                }

                else -> {
                    throw IllegalArgumentException(
                        "Unknown highscore preference: $name"
                    )
                }
            }
        }

        reader.endObject()

        check(editor.commit()) {
            "Could not persist restored highscore preferences"
        }

        return reader
    }

    companion object {
        private const val BACKUP_KEY = "highscore"
        private const val PREFERENCES_FILE = "pfa-math-highscore"
        private const val CONTINUE_KEY = "continue"

        private val STRING_KEYS = Regex("(hs|previous).*")
        private val INTEGER_KEYS = Regex("(right|wrong).*")
    }
}