package fortunate.signature.penit.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import fortunate.signature.penit.entities.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(indices = [Index(value = ["id", "folder", "due", "timestamp", "dueDate"])])
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val folder: TaskFolder,
    val name: String,
    val description: String,
    val due: Boolean,
    var priority: Priority,
    val timestamp: Long,
    val dueDate: String
) : TaskItem(TaskViewType.TASK), Parcelable {

    companion object {

        fun createTask(id: Long, folder: TaskFolder, name: String, description: String, due: Boolean, priority: Priority, timestamp: Long, dueDate: String): Task {
            return Task(id, folder, name, description, due, priority, timestamp, dueDate)
        }
    }
}