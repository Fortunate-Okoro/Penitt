package fortunate.signature.penit.data.viewmodel

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import fortunate.signature.penit.R
import fortunate.signature.penit.data.TaskDatabase
import fortunate.signature.penit.data.livedata.*
import fortunate.signature.penit.entities.TaskFolder
import fortunate.signature.penit.misc.getLocale
import fortunate.signature.penit.model.*
import fortunate.signature.penit.xml.XMLTaskUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BaseTaskVM (private val app: Application) : AndroidViewModel(app) {
    private val database = TaskDatabase.getDatabase(app)
    private val taskDao = database.taskDao

    var currentFile: File? = null

    val formatter = getDateFormatter(app.getLocale())

    val tasks = TaskContent(taskDao.getFrom(TaskFolder.TASKS), ::transform)
    val deletedTasks = TaskContent(taskDao.getFrom(TaskFolder.DELETED), ::transform)
    val finishedTasks = TaskContent(taskDao.getFrom(TaskFolder.FINISHED), ::transform)

    var keyword = String()
        set(value) {
            if (field != value) {
                field = value
                searchResults.fetch(value)
            }
        }

    val searchResults = TaskSearchResult(viewModelScope, taskDao, ::transform)

    private val dueTask = TaskHeader(app.getString(R.string.due))
    private val others = TaskHeader(app.getString(R.string.others))

    init {
        viewModelScope.launch {
            val previousTask = getPreviousTasks()
            val delete: (file: File) -> Unit = { file: File -> file.delete() }
            if (previousTask.isNotEmpty()) {
                database.withTransaction {
                    taskDao.insert(previousTask)
                    getTaskPath().listFiles()?.forEach(delete)
                    getDeletedPath().listFiles()?.forEach(delete)
                    getFinishedPath().listFiles()?.forEach(delete)
                }
            }
        }
    }

    private fun transform(list: List<Task>): List<TaskItem> {
        if (list.isEmpty()) {
            return list
        } else {
            val firstTask = list[0]
            return if (firstTask.due) {
                val newList = ArrayList<TaskItem>(list.size + 2)
                newList.add(dueTask)

                val indexFirstUndueTask = list.indexOfFirst { task -> !task.due }
                list.forEachIndexed { index, task ->
                    if (index == indexFirstUndueTask) {
                        newList.add(others)
                    }
                    newList.add(task)
                }
                newList
            } else list
        }
    }

    fun dueTask(id: Long) = executeAsync { taskDao.updateDue(id, true) }

    fun undueTask(id: Long) = executeAsync { taskDao.updateDue(id, false) }

    fun deleteAllTasks() = executeAsync { taskDao.deleteFrom(TaskFolder.DELETED) }

    fun restoreTask(id: Long) = executeAsync { taskDao.move(id, TaskFolder.TASKS) }

    fun moveTaskToDeleted(id: Long) = executeAsync { taskDao.move(id, TaskFolder.DELETED) }

    fun moveTaskToArchive(id: Long) = executeAsync { taskDao.move(id, TaskFolder.FINISHED) }

    fun deleteTaskForever(task: Task) = executeAsync { taskDao.delete(task) }

    private fun getPreviousTasks(): List<Task> {
        val previousTasks = java.util.ArrayList<Task>()
        getTaskPath().listFiles()?.mapTo(previousTasks) { file ->
            XMLTaskUtils.readTaskFromFile(
                file,
                TaskFolder.TASKS
            )
        }
        getDeletedPath().listFiles()?.mapTo(previousTasks) { file ->
            XMLTaskUtils.readTaskFromFile(
                file,
                TaskFolder.DELETED
            )
        }
        getFinishedPath().listFiles()?.mapTo(previousTasks) { file ->
            XMLTaskUtils.readTaskFromFile(
                file,
                TaskFolder.FINISHED
            )
        }
        return previousTasks
    }

    private fun getTaskPath() = getFolder("tasks")

    private fun getDeletedPath() = getFolder("deleted")

    private fun getFinishedPath() = getFolder("finished")

    private fun getFolder(name: String): File {
        val folder = File(app.filesDir, name)
        if (!folder.exists()) {
            folder.mkdir()
        }
        return folder
    }

    private fun executeAsync(function: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { function() }
        }
    }

    fun writeCurrentFileToUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                (app.contentResolver.openOutputStream(uri) as? FileOutputStream)?.use { stream ->
                    stream.channel.truncate(0)
                    stream.write(requireNotNull(currentFile).readBytes())
                }
            }
            Toast.makeText(app, R.string.saved_to_device, Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        fun getDateFormatter(locale: Locale): SimpleDateFormat {
            val pattern = when (locale.language) {
                Locale.CHINESE.language,
                Locale.JAPANESE.language -> "yyyy年 MMM d日 (EEE)"
                else -> "MMM dd yyyy, h:mm aa"
            }
            return SimpleDateFormat(pattern, locale)
        }
    }
}