package fortunate.signature.penit.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fortunate.signature.penit.data.TaskDatabase
import fortunate.signature.penit.entities.*
import fortunate.signature.penit.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val database = TaskDatabase.getDatabase(app)
    private val taskDao = database.taskDao

    var isNewTask = true
    var isFirstInstance = true

    var id = 0L
    var folder = TaskFolder.TASKS
    var title = String()
    var description = String()
    var due = false
    var priority = setPriority()
    var timestamp = Date().time
    var dueDate = String()

    fun setStateFromTask(task: Task) {
        id = task.id
        folder = task.folder

        title = task.name
        description = task.description
        due = task.due
        priority = task.priority
        timestamp = task.timestamp
        dueDate = task.dueDate
    }

    fun saveTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            id = withContext(Dispatchers.IO) { taskDao.insert(getTask()) }
            onComplete()
        }
    }

    fun deleteTaskForever(onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { taskDao.delete(getTask()) }
            onComplete()
        }
    }

    fun restoreTask() {
        folder = TaskFolder.TASKS
    }

    fun moveTaskToFinished() {
        folder = TaskFolder.FINISHED
    }

    fun moveTaskToDeleted() {
        folder = TaskFolder.DELETED
    }

    private fun setPriority(): Priority {
        return when (String()) {
            "Important Priority" -> Priority.IMPORTANT
            "Basic Priority" -> Priority.BASIC
            "Low Priority" -> Priority.LOW
            else -> Priority.NONE
        }
    }

    private fun getTask() = Task(id, folder, title, description, due, priority, timestamp, dueDate)

    class Factory(private val app: Application) : ViewModelProvider.AndroidViewModelFactory(app) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(app) as T
        }
    }

}