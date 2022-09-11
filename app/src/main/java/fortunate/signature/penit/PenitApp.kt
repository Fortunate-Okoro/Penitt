package fortunate.signature.penit

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import fortunate.signature.penit.helpers.SettingsHelper.AppearanceColor
import fortunate.signature.penit.helpers.SettingsHelper.Font
import fortunate.signature.penit.helpers.SettingsHelper.Theme

class PenitApp : Application() , SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate() {
        super.onCreate()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(preferences)
        setFont(preferences)

        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String?) {
        when (key) {
            getThemeKey() -> {
                setTheme(preferences)
            }
            getFontKey() -> {
                setFont(preferences)
            }
            getAppearanceKey() -> {
                setAppearance(preferences)
            }
        }
    }

    private fun setTheme(preferences: SharedPreferences) {
        val key = getThemeKey()
        val default = Theme.defaultValue
        when (preferences.getString(key, default)) {
            Theme.dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.followSystem -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setFont(preferences: SharedPreferences) {
        val key = getFontKey()
        val default = Font.defaultValue
        when (preferences.getString(key, default)) {
//            Font.andada -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            Font.quintessential -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            Font.merriweather -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setAppearance(preferences: SharedPreferences) {
        val key = getAppearanceKey()
        val default = AppearanceColor.defaultValue
        when (preferences.getString(key, default)) {
//            Font.andada -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            Font.quintessential -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            Font.merriweather -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun getThemeKey() = Theme.key
    private fun getFontKey() = Font.key
    private fun getAppearanceKey() = AppearanceColor.key


}