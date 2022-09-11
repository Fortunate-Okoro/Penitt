package fortunate.signature.penit.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import fortunate.signature.penit.databinding.TextLayoutBinding
import fortunate.signature.penit.model.Header

class HeaderVH(private val binding: TextLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        val params = binding.root.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
    }

    fun bind(header: Header) {
        binding.root.text = header.label
    }
}