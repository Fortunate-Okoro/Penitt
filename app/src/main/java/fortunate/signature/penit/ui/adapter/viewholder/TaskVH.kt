package fortunate.signature.penit.ui.adapter.viewholder

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.R
import fortunate.signature.penit.databinding.TaskBinding
import fortunate.signature.penit.entities.Priority
import fortunate.signature.penit.model.Task

class TaskVH(
    private val binding: TaskBinding,
    listener: ItemListener,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.taskCard.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.taskCard.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun bind(task: Task) {

        binding.taskCard.transitionName = task.id.toString()
        val date = "Due for ${task.dueDate}"
        if (task.due) {
            binding.btnTaskState.setImageResource(R.drawable.ic_retry)
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTaskTitle.text = spannableString
            val spannableDescription = SpannableString(task.description)
            spannableDescription.setSpan(
                StrikethroughSpan(),
                0,
                spannableDescription.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTaskDescription.text = spannableDescription

            val spannableDate = SpannableString(date)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTaskDueDate.text = spannableDate
        } else {
            binding.btnTaskState.setImageResource(R.drawable.ic_done)
            binding.tvTaskTitle.text = task.name
            binding.tvTaskDescription.text = task.description
            binding.tvTaskDueDate.text = date
        }

        when (task.priority) {
            Priority.NONE -> {
                binding.priorityState.setBackgroundColor(ContextCompat.getColor(binding.priorityState.context, R.color.white))
            }
            Priority.IMPORTANT -> {
                binding.priorityState.setBackgroundColor(ContextCompat.getColor(binding.priorityState.context, R.color.red))
            }
            Priority.BASIC -> {
                binding.priorityState.setBackgroundColor(ContextCompat.getColor(binding.priorityState.context, R.color.green))
            }
            Priority.LOW -> {
                binding.priorityState.setBackgroundColor(ContextCompat.getColor(binding.priorityState.context, R.color.yellow))
            }
        }

    }

}
