package fortunate.signature.penit.model

import androidx.room.Ignore
import fortunate.signature.penit.entities.TaskViewType

sealed class TaskItem(@Ignore val viewType: TaskViewType)