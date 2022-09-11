package fortunate.signature.penit.data.livedata

import androidx.lifecycle.LiveData
import fortunate.signature.penit.model.*

class TaskContent(liveData: LiveData<List<Task>>, transform: (List<Task>) -> List<TaskItem>) : LiveData<List<TaskItem>>() {

    init {
        liveData.observeForever { list -> value = transform(list) }
    }
}