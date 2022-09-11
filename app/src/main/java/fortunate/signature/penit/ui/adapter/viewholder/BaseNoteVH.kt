package fortunate.signature.penit.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.R
import fortunate.signature.penit.databinding.BaseNoteBinding
import fortunate.signature.penit.databinding.ListItemPreviewBinding
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.model.BaseNote
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class BaseNoteVH(
    private val binding: BaseNoteBinding,
    private val settingsHelper: SettingsHelper,
    listener: ItemListener,
    private val prettyTime: PrettyTime,
    private val formatter: SimpleDateFormat,
    private val inflater: LayoutInflater,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvNote.maxLines = settingsHelper.getMaxLines()

        binding.cardView.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.cardView.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun bind(baseNote: BaseNote) {
        bindNote(baseNote)

        binding.tvTitle.text = baseNote.title
        binding.tvTitle.isVisible = baseNote.title.isNotEmpty()

        val date = Date(baseNote.timestamp)
        binding.tvDate.isVisible = settingsHelper.showDateCreated()
        when (settingsHelper.getDateFormat()) {
            SettingsHelper.DateFormat.relative -> binding.tvDate.text = prettyTime.format(date)
            SettingsHelper.DateFormat.absolute -> binding.tvDate.text = formatter.format(date)
        }

        binding.labelGroup.bindLabels(baseNote.labels)

        if (isEmpty(baseNote)) {
            binding.tvNote.setText(R.string.empty_note)
            binding.tvNote.isVisible = true
        }
    }

    private fun bindNote(note: BaseNote) {

        binding.tvNote.text = note.body.applySpans(note.spans)
        binding.tvNote.isVisible = note.body.isNotEmpty()

        if (note.items.isEmpty()) {
            binding.llList.visibility = View.GONE
        } else {
            binding.llList.visibility = View.VISIBLE
            binding.llList.removeAllViews()

            val max = settingsHelper.getMaxItems()

            note.items.take(max).forEach { item ->
                val view = ListItemPreviewBinding.inflate(inflater, binding.llList, true).root
                view.text = item.body
                handleChecked(view, item.checked)
            }

            if (note.items.size > max) {
                val view = ListItemPreviewBinding.inflate(inflater, binding.llList, true).root
                val itemsRemaining = note.items.size - max
                view.text = if (itemsRemaining == 1) {
                    binding.root.context.getString(R.string.one_more_item)
                } else binding.root.context.getString(R.string.more_items, itemsRemaining)
            }
        }
    }

    private fun isEmpty(baseNote: BaseNote): Boolean {
        return baseNote.title.isBlank() && baseNote.body.isBlank() && baseNote.items.isEmpty()
    }

    private fun handleChecked(textView: TextView, checked: Boolean) {
        if (checked) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_checkbox, 0, 0, 0)
        } else textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_checkbox_outline, 0, 0, 0)
        textView.paint.isStrikeThruText = checked
    }
}