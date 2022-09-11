package fortunate.signature.penit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.NoteViewModel
import fortunate.signature.penit.entities.Folder
import fortunate.signature.penit.helpers.OperationsParent
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.model.*
import kotlinx.coroutines.launch


abstract class NoteBaseActivity : AppCompatActivity(), OperationsParent {

    internal abstract val binding: ViewBinding
    internal val model: NoteViewModel by viewModels { NoteViewModel.Factory(application) }

    override fun onBackPressed() {
        model.saveNote { super.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        model.saveNote {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.root.isSaveFromParentEnabled = false

        if (model.isFirstInstance) {
            val selectedBaseNote = intent.getParcelableExtra<BaseNote>(Constants.SelectedBaseNote)
            if (selectedBaseNote != null) {
                model.isNewNote = false
                model.setStateFromBaseNote(selectedBaseNote)
            } else model.isNewNote = true

            if (intent.action == Intent.ACTION_SEND) {
                receiveSharedNote()
            }

            model.isFirstInstance = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val pin = menu.add(R.string.pin, R.drawable.ic_pin) { item -> pin(item) }
            bindPinned(pin)

            menu.add(R.string.share, R.drawable.ic_share) { share() }
            menu.add(R.string.library, R.drawable.ic_library_notes) { label() }

            when (model.folder) {
                Folder.NOTES -> {
                    menu.add(R.string.delete, R.drawable.ic_delete) { delete() }
                    menu.add(R.string.archive, R.drawable.ic_archive) { archive() }
                }
                Folder.DELETED -> {
                    menu.add(R.string.restore, R.drawable.ic_restore) { restore() }
                    menu.add(R.string.delete_forever, R.drawable.ic_delete) { deleteForever() }
                }
                Folder.ARCHIVED -> {
                    menu.add(R.string.unarchive, R.drawable.ic_unarchive) { restore() }
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun accessContext(): Context {
        return this
    }

    override fun insertLabel(label: Label, onComplete: (success: Boolean) -> Unit) {
        model.insertLabel(label, onComplete)
    }

    abstract fun getLabelGroup(): ChipGroup

    open fun receiveSharedNote() {}

    private fun share() {
        val body = model.body
        val list = model.items.getBody()
        val view = "$body \n $list"
        Operations.shareNote(this, model.title, view)
    }

    private fun label() {
        lifecycleScope.launch {
            val labels = model.getAllLabelsAsList()
            labelNote(labels, model.labels) { updatedLabels ->
                model.labels = updatedLabels
                getLabelGroup().bindLabels(updatedLabels)
            }
        }
    }

    private fun delete() {
        model.moveBaseNoteToDeleted()
        onBackPressed()
    }

    private fun restore() {
        model.restoreBaseNote()
        onBackPressed()
    }

    private fun archive() {
        model.moveBaseNoteToArchive()
        onBackPressed()
    }

    private fun deleteForever() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.delete_note_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                model.deleteBaseNoteForever {
                    super.onBackPressed()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun pin(item: MenuItem) {
        model.pinned = !model.pinned
        bindPinned(item)
    }

    private fun bindPinned(item: MenuItem) {
        val icon: Int
        val title: Int
        if (model.pinned) {
            icon = R.drawable.ic_unpin
            title = R.string.unpin
        } else {
            icon = R.drawable.ic_pin
            title = R.string.pin
        }
        item.setTitle(title)
        item.setIcon(icon)
    }

    internal fun setupToolbar(toolbar: MaterialToolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}