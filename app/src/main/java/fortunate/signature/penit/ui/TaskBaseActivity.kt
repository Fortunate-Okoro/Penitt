package fortunate.signature.penit.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.TaskViewModel
import fortunate.signature.penit.entities.TaskFolder
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.model.Task

abstract class TaskBaseActivity : AppCompatActivity() {

    internal abstract val binding: ViewBinding
    internal val model: TaskViewModel by viewModels { TaskViewModel.Factory(application) }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        model.saveTask {}
    }

    override fun onBackPressed() {
        model.saveTask { super.onBackPressed() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.root.isSaveFromParentEnabled = false

        if (model.isFirstInstance) {
            val selectedTask = intent.getParcelableExtra<Task>(Constants.SelectedTask)
            if (selectedTask != null) {
                model.isNewTask = false
                model.setStateFromTask(selectedTask)
            } else model.isNewTask = true

            if (intent.action == Intent.ACTION_SEND) {
                receiveSharedNote()
            }

            model.isFirstInstance = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val due= menu.add(R.string.ovedue_task, R.drawable.ic_timelapse) { item -> pin(item) }
            bindDue(due)

            when (model.folder) {
                TaskFolder.TASKS -> {
                    menu.add(R.string.delete, R.drawable.ic_delete) { delete() }
                    menu.add(R.string.archive, R.drawable.ic_archive) { archive() }
                }
                TaskFolder.DELETED -> {
                    menu.add(R.string.restore, R.drawable.ic_restore) { restore() }
                    menu.add(R.string.delete_forever, R.drawable.ic_delete) { deleteForever() }
                }
                TaskFolder.FINISHED -> {
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

    open fun receiveSharedNote() {}

    private fun delete() {
        model.moveTaskToDeleted()
        onBackPressed()
    }

    private fun restore() {
        model.restoreTask()
        onBackPressed()
    }

    private fun archive() {
        model.moveTaskToFinished()
        onBackPressed()
    }

    private fun deleteForever() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.delete_note_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                model.deleteTaskForever {
                    super.onBackPressed()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun pin(item: MenuItem) {
        model.due = !model.due
        bindDue(item)
    }

    private fun bindDue(item: MenuItem) {
        val icon: Int
        val title: Int
        if (model.due) {
            icon = R.drawable.ic_timenotlapse
            title = R.string.unpin
        } else {
            icon = R.drawable.ic_timelapse
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