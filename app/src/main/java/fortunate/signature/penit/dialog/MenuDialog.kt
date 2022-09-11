package fortunate.signature.penit.dialog

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog
import fortunate.signature.penit.databinding.MenuItemBinding
import fortunate.signature.penit.databinding.MenuTitleBinding

class MenuDialog(context: Context) : BottomSheetDialog(context) {

    private val linearLayout: LinearLayout

    init {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = params
        linearLayout.orientation = LinearLayout.VERTICAL

        val scrollView = NestedScrollView(context)
        scrollView.layoutParams = params

        scrollView.addView(linearLayout)
        setContentView(scrollView)
    }

    fun dialogTitle(title: Int) : MenuDialog {
        val titleItem = MenuTitleBinding.inflate(layoutInflater).root
        titleItem.setText(title)
        linearLayout.addView(titleItem)
        return this
    }

    fun add(title: Int, drawable: Int = 0, onClick: () -> Unit): MenuDialog {
        val item = MenuItemBinding.inflate(layoutInflater).root
        item.setText(title)
        item.setOnClickListener {
            dismiss()
            onClick()
        }
        item.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0)
        linearLayout.addView(item)
        return this
    }

    fun addTitle(title: Int, onClick: () -> Unit): MenuDialog {
        val item = MenuItemBinding.inflate(layoutInflater).root
        item.setText(title)
        item.setOnClickListener {
            dismiss()
            onClick()
        }
        linearLayout.addView(item)
        return this
    }

}