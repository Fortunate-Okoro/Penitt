package fortunate.signature.penit.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.text.InputType
import android.view.MotionEvent
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.databinding.NoteListItemBinding
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.setOnNextAction
import fortunate.signature.penit.model.ListItem

@SuppressLint("ClickableViewAccessibility")
class MakeListVH(
    val binding: NoteListItemBinding,
    private val settingsHelper: SettingsHelper,
    listener: ListItemListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.listItem.setOnNextAction {
            listener.onMoveToNext(adapterPosition)
        }

        binding.listItem.doAfterTextChanged { text ->
            listener.afterTextChange(adapterPosition, text.toString().trim())
        }

        binding.cbList.setOnCheckedChangeListener { _, isChecked ->
            binding.listItem.paint.isStrikeThruText = isChecked
            binding.listItem.isEnabled = !isChecked

            listener.onCheckedChange(adapterPosition, isChecked)
        }

        binding.ivDrag.setOnTouchListener { _, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                listener.onStartDrag(this)
            }
            false
        }
    }

    fun bind(item: ListItem) {
        binding.listItem.setText(item.body)
        binding.cbList.isChecked = item.checked
        binding.listItem.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)

        val size = settingsHelper.getFontSize().toFloat()

        binding.listItem.textSize = size
    }

}