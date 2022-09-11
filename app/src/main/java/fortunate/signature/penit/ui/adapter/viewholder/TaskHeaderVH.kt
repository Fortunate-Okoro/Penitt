package fortunate.signature.penit.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import fortunate.signature.penit.databinding.TextLayoutBinding
import fortunate.signature.penit.model.TaskHeader

class TaskHeaderVH(private val binding: TextLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        val params = binding.root.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
    }

    fun bind(header: TaskHeader) {
        binding.root.text = header.label
    }
}