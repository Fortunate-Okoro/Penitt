package fortunate.signature.penit.data.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import fortunate.signature.penit.data.dao.TaskDao
import fortunate.signature.penit.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskSearchResult(
    private val scope: CoroutineScope,
    private val taskDao: TaskDao,
    transform: (List<Task>) -> List<TaskItem>
) : LiveData<List<TaskItem>>() {

    private var job: Job? = null
    private var liveData: LiveData<List<Task>>? = null
    private val observer = Observer<List<Task>> { list -> value = transform(list) }

    init {
        value = emptyList()
    }

    fun fetch(keyword: String) {
        job?.cancel()
        liveData?.removeObserver(observer)
        job = scope.launch {
            if (keyword.isNotEmpty()) {
                liveData = taskDao.getTasksByKeyword(keyword)
                liveData?.observeForever(observer)
            } else value = emptyList()
        }
    }
}