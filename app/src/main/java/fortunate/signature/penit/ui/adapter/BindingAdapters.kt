package fortunate.signature.penit.ui.adapter

import android.view.View
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import fortunate.signature.penit.R
import fortunate.signature.penit.entities.Priority

object BindingAdapters {

    @BindingAdapter("parsePriorityToInt")
    @JvmStatic
    fun parsePriorityToInt(view: Spinner, priority: Priority?) {
        when (priority) {
            Priority.NONE -> view.setSelection(0)
            Priority.IMPORTANT -> view.setSelection(1)
            Priority.BASIC -> view.setSelection(2)
            Priority.LOW -> view.setSelection(3)
            else -> view.setSelection(0)
        }
    }

    @BindingAdapter("parsePriorityColor")
    @JvmStatic
    fun parsePriorityColor(view: View, priority: Priority) {
        when (priority) {
            Priority.NONE -> {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.white
                    )
                )
            }
            Priority.IMPORTANT -> {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.red
                    )
                )
            }
            Priority.BASIC -> {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            }
            Priority.LOW -> {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.yellow
                    )
                )
            }
        }
    }

}

