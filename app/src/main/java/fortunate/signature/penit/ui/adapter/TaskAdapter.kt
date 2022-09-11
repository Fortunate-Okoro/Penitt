package fortunate.signature.penit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.databinding.TaskBinding
import fortunate.signature.penit.databinding.TextLayoutBinding
import fortunate.signature.penit.entities.TaskViewType
import fortunate.signature.penit.model.*
import fortunate.signature.penit.ui.adapter.viewholder.ItemListener
import fortunate.signature.penit.ui.adapter.viewholder.TaskHeaderVH
import fortunate.signature.penit.ui.adapter.viewholder.TaskVH

class TaskAdapter(
    private val listener: ItemListener
) : ListAdapter<TaskItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TaskHeader -> (holder as TaskHeaderVH).bind(item)
            is Task -> (holder as TaskVH).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (TaskViewType.values()[viewType]) {
            TaskViewType.TASK -> {
                val binding = TaskBinding.inflate(inflater, parent, false)
                TaskVH(binding, listener)
            }
            TaskViewType.HEADER -> {
                val binding = TextLayoutBinding.inflate(inflater, parent, false)
                TaskHeaderVH(binding)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TaskItem>() {

        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return when (oldItem) {
                is Task -> if (newItem is Task) {
                    oldItem.id == newItem.id
                } else false
                is TaskHeader -> if (newItem is TaskHeader) {
                    oldItem.label == newItem.label
                }
                else false
            }
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return when (oldItem) {
                is Task -> if (newItem is Task) {
                    oldItem == newItem
                } else false
                is TaskHeader -> if (newItem is TaskHeader) {
                    oldItem.label == newItem.label
                } else false
            }
        }
    }
}
