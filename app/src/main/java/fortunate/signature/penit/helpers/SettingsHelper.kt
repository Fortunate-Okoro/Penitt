package fortunate.signature.penit.helpers

import android.content.Context
import androidx.preference.PreferenceManager
import fortunate.signature.penit.R

class SettingsHelper (context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getMaxItems() = preferences.getInt(MaxItems.key, MaxItems.defaultValue)

    fun getMaxLines() = preferences.getInt(MaxLines.key, MaxLines.defaultValue)

    fun getFontSize() = preferences.getInt(FontSize.key, FontSize.defaultValue)

    fun showDateCreated() = getDateFormat() != DateFormat.none

    fun getDateFormat() = preferences.getString(DateFormat.key, DateFormat.defaultValue)

    interface ListInfo {

        val title: Int

        val key: String
        val defaultValue: String

        fun getRawEntries(): Array<Int>

        fun getEntryValues(): Array<String>

        fun getEntries(context: Context): Array<String> {
            val rawEntries = getRawEntries()
            return Array(rawEntries.size) { index ->
                val id = rawEntries[index]
                context.getString(id)
            }
        }
    }

    interface SeekbarInfo {

        val title: Int

        val key: String
        val defaultValue: Int

        val min: Int
        val max: Int
    }

    object Theme : ListInfo {
        const val dark = "dark"
        const val light = "light"
        const val followSystem = "followSystem"

        override val title = R.string.theme
        override val key = "theme"
        override val defaultValue = followSystem

        override fun getRawEntries() = arrayOf(R.string.dark_theme, R.string.light_theme, R.string.follow_system)

        override fun getEntryValues() = arrayOf(dark, light, followSystem)
    }

    object DateFormat : ListInfo {
        const val none = "none"
        const val relative = "relative"
        const val absolute = "absolute"

        override val title = R.string.date_format
        override val key = "dateFormat"
        override val defaultValue = relative

        override fun getRawEntries() = arrayOf(R.string.none, R.string.relative, R.string.absolute)

        override fun getEntryValues() = arrayOf(none, relative, absolute)
    }

    object MaxItems : SeekbarInfo {

        override val title = R.string.max_items_to_display

        override val key = "maxItemsToDisplayInList.v1"
        override val defaultValue = 3

        override val min = 1
        override val max = 5
    }

    object MaxLines : SeekbarInfo {

        override val title = R.string.max_lines_to_display

        override val key = "maxLinesToDisplayInNote.v1"
        override val defaultValue = 3

        override val min = 1
        override val max = 5
    }

    object Font : ListInfo {
        const val andada = "andada"
        const val quintessential = "quintessential"
        const val merriweather = "merriweather"

        override val title = R.string.font
        override val key = "font"
        override val defaultValue = andada

        override fun getRawEntries() = arrayOf(R.string.andada, R.string.quintessential, R.string.merriweather)

        override fun getEntryValues() = arrayOf(andada, quintessential, merriweather)

    }

    object FontSize : SeekbarInfo {

        override val title = R.string.fontSize

        override val key = "fontSize.v1"
        override val defaultValue = 18

        override val min = 14
        override val max = 22

    }

    object AppearanceColor : ListInfo{
        const val fresh = R.color.penitFresh.toString() //"#CC397B"
        const val basic =  R.color.penitBasic.toString() //"#5030BD"
        const val bright =  R.color.penitBright.toString() //"#228B22"

        override val title = R.string.appearance
        override val key = "appearance"
        override val defaultValue = fresh

        override fun getRawEntries() = arrayOf(R.string.penitFresh, R.string.penitBasic, R.string.penitBright)

        override fun getEntryValues() = arrayOf(fresh, basic, bright)
    }
}