package fortunate.signature.penit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.databinding.BaseNoteBinding
import fortunate.signature.penit.databinding.TextLayoutBinding
import fortunate.signature.penit.entities.ViewType
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.model.*
import fortunate.signature.penit.ui.adapter.viewholder.BaseNoteVH
import fortunate.signature.penit.ui.adapter.viewholder.HeaderVH
import fortunate.signature.penit.ui.adapter.viewholder.ItemListener
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat

class BaseNoteAdapter(
    private val settingsHelper: SettingsHelper,
    private val formatter: SimpleDateFormat,
    private val listener: ItemListener
) : ListAdapter<Item, RecyclerView.ViewHolder>(DiffCallback) {

    private val prettyTime = PrettyTime()

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Header -> (holder as HeaderVH).bind(item)
            is BaseNote -> (holder as BaseNoteVH).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (ViewType.values()[viewType]) {
            ViewType.NOTE -> {
                val binding = BaseNoteBinding.inflate(inflater, parent, false)
                BaseNoteVH(binding, settingsHelper, listener, prettyTime, formatter, inflater)
            }
            ViewType.HEADER -> {
                val binding = TextLayoutBinding.inflate(inflater, parent, false)
                HeaderVH(binding)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return when (oldItem) {
                is BaseNote -> if (newItem is BaseNote) {
                    oldItem.id == newItem.id
                } else false
                is Header -> if (newItem is Header) {
                    oldItem.label == newItem.label
                }
                else false
            }
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return when (oldItem) {
                is BaseNote -> if (newItem is BaseNote) {
                    oldItem == newItem
                } else false
                is Header -> if (newItem is Header) {
                    oldItem.label == newItem.label
                } else false
            }
        }
    }
}