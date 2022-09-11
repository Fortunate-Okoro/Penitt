package fortunate.signature.penit.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.databinding.TextLayoutBinding

class LabelVH(private val binding: TextLayoutBinding, listener: ItemListener) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.root.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun bind(value: String) {
        binding.root.text = value
    }
}