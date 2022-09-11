package fortunate.signature.penit.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.*
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.BaseNoteVM
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.Constants

class Settings : PreferenceFragmentCompat() {

    private val model: BaseNoteVM by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val appearance = getPreferenceCategory(context, R.string.appearance)
        val backup = getPreferenceCategory(context, R.string.backup)
        val sync = getPreferenceCategory(context, R.string.sync)
        val about = getPreferenceCategory(context, R.string.about)
        val followUS = getPreferenceCategory(context, R.string.follow_us)

        screen.addPreference(appearance)
        screen.addPreference(backup)
        screen.addPreference(about)
        screen.addPreference(sync)
        screen.addPreference(followUS)

        appearance.addPreference(getListPreference(context, SettingsHelper.Theme))
        appearance.addPreference(getListPreference(context, SettingsHelper.DateFormat))
        appearance.addPreference(getSeekbarPreference(context, SettingsHelper.MaxItems))
        appearance.addPreference(getSeekbarPreference(context, SettingsHelper.MaxLines))

        val font = getPreference(context, R.string.font)
        appearance.addPreference(getListPreference(context, SettingsHelper.Font))//font)//Font
        appearance.addPreference(getSeekbarPreference(context, SettingsHelper.FontSize))
        appearance.addPreference(getListPreference(context, SettingsHelper.AppearanceColor))//ColorSecondary

        val importBackup = getPreference(context, R.string.import_backup)
        val exportBackup = getPreference(context, R.string.export_backup)

        backup.addPreference(importBackup)
        backup.addPreference(exportBackup)

        val rate = getPreference(context, R.string.rate)
        val share = getPreference(context, R.string.share_with)
        val changeLanguage = getPreference(context, R.string.change_language)
        val version = getPreference(context, R.string.version)

        about.addPreference(share)
        about.addPreference(rate)
        about.addPreference(changeLanguage)
        about.addPreference(version)

        val googleAccount = getPreference(context, R.string.google)
        val syncMode = getPreference(context, R.string.sync_mode)

        sync.addPreference(googleAccount)
        sync.addPreference(syncMode)

        val linkedIn = getPreference(context, R.string.linkedIn)
        val twitter = getPreference(context, R.string.twitter)

        followUS.addPreference(linkedIn)
        followUS.addPreference(twitter)

        disableIconSpace(screen)

        preferenceScreen = screen

        font.setOnPreferenceClickListener {
//            findNavController().navigate(R.id.SettingsToFont)
            return@setOnPreferenceClickListener true
        }

        exportBackup.setOnPreferenceClickListener {
            exportBackup()
            return@setOnPreferenceClickListener true
        }

        importBackup.setOnPreferenceClickListener {
            importBackup()
            return@setOnPreferenceClickListener true
        }

        rate.setOnPreferenceClickListener {
            openLink("https://play.google.com/store/apps/details?id=fortunate.com.penit")
            return@setOnPreferenceClickListener true
        }

        linkedIn.setOnPreferenceClickListener {
            openLink("https://linkedin.com/in/okoro-fortunate")
            return@setOnPreferenceClickListener true
        }

        twitter.setOnPreferenceClickListener {
            openLink("https://twitter.com/OkoroFC")
            return@setOnPreferenceClickListener true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                when (requestCode) {
                    RequestCodeImportFile -> model.importBackup(uri)
                    Constants.RequestCodeExportFile -> model.exportBackup(uri)
                }
            }
        }
    }

    private fun disableIconSpace(group: PreferenceGroup) {
        for (index in 0 until group.preferenceCount) {
            val preference = group.getPreference(index)
            preference.isIconSpaceReserved = false
            if (preference is PreferenceGroup) {
                disableIconSpace(preference)
            }
        }
    }

    private fun exportBackup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "text/xml"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, "Penit Backup")
        startActivityForResult(intent, Constants.RequestCodeExportFile)
    }

    private fun importBackup() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "text/xml"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, RequestCodeImportFile)
    }

    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {

        private const val RequestCodeImportFile = 20

        private fun getPreference(context: Context, title: Int): Preference {
            val preference = Preference(context)
            preference.setTitle(title)
            return preference
        }

        private fun getPreferenceCategory(context: Context, title: Int): PreferenceCategory {
            val category = PreferenceCategory(context)
            category.setTitle(title)
            return category
        }

        private fun getListPreference(context: Context, info: SettingsHelper.ListInfo): ListPreference {
            val preference = ListPreference(context)
            preference.setTitle(info.title)
            preference.setDialogTitle(info.title)

            preference.key = info.key
            preference.entries = info.getEntries(context)
            preference.entryValues = info.getEntryValues()
            preference.setDefaultValue(info.defaultValue)
            preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

            return preference
        }

        private fun getSeekbarPreference(context: Context, info: SettingsHelper.SeekbarInfo): SeekBarPreference {
            val preference = SeekBarPreference(context)
            preference.setTitle(info.title)

            preference.key = info.key
            preference.setDefaultValue(info.defaultValue)

            preference.min = info.min
            preference.max = info.max
            preference.showSeekBarValue = true

            return preference
        }
    }
}