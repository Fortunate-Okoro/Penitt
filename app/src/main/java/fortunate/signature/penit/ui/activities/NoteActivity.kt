package fortunate.signature.penit.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.style.*
import android.util.Patterns
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.BaseNoteVM
import fortunate.signature.penit.databinding.ActivityNoteBinding
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.model.ListItem
import fortunate.signature.penit.ui.NoteBaseActivity
import fortunate.signature.penit.ui.adapter.MakeListAdapter
import fortunate.signature.penit.ui.adapter.viewholder.ListItemListener
import fortunate.signature.penit.ui.adapter.viewholder.MakeListVH
import fortunate.signature.penit.utils.LinkMovementMethod
import java.util.*

class NoteActivity : NoteBaseActivity() {

    private lateinit var adapter: MakeListAdapter
    private lateinit var settingsHelper: SettingsHelper
    override val binding by lazy { ActivityNoteBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsHelper = SettingsHelper(this)

        binding.etTitle.setOnNextAction {
            binding.etBody.requestFocus()
        }

        setupEditor()
        setupListeners()
        setupRecyclerView()
        setupTextSize()
        setupToolbar(binding.toolbar)
        supportActionBar?.title = "Write a note"

        if (model.isNewNote) {
            binding.etTitle.requestFocus()
        }

        binding.addItem.setOnClickListener {
            addListItem()
        }

        setStateFromModel()
    }

    private fun addListItem() {
        val position = model.items.size
        val listItem = ListItem(String(), false)
        model.items.add(listItem)
        adapter.notifyItemInserted(position)
        binding.rvList.post {
            val viewHolder = binding.rvList.findViewHolderForAdapterPosition(position) as MakeListVH?
            viewHolder?.binding?.listItem?.requestFocus()
        }
    }

    private fun setupRecyclerView() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val drag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipe = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(drag, swipe)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                model.items.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Collections.swap(model.items, viewHolder.adapterPosition, target.adapterPosition)
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
        })

        adapter = MakeListAdapter(model.items, settingsHelper, object : ListItemListener {

            override fun onMoveToNext(position: Int) {
                moveToNext(position)
            }

            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }

            override fun afterTextChange(position: Int, text: String) {
                model.items[position].body = text
            }

            override fun onCheckedChange(position: Int, checked: Boolean) {
                model.items[position].checked = checked
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvList)

        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(this)
    }

    private fun moveToNext(currentPosition: Int) {
        val viewHolder = binding.rvList.findViewHolderForAdapterPosition(currentPosition + 1) as MakeListVH?
        if (viewHolder != null) {
            if (viewHolder.binding.cbList.isChecked) {
                moveToNext(currentPosition + 1)
            } else viewHolder.binding.listItem.requestFocus()
        } else addListItem()
    }

    override fun receiveSharedNote() {
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)

        val string = intent.getStringExtra(Intent.EXTRA_TEXT)
        val charSequence = intent.getCharSequenceExtra(Operations.extraCharSequence)
        val body = charSequence ?: string

        if (body != null) {
            model.body = Editable.Factory.getInstance().newEditable(body)
        }
        if (title != null) {
            model.title = title
        }
        Toast.makeText(this, R.string.saved_to_penit, Toast.LENGTH_SHORT).show()
    }

    override fun getLabelGroup() = binding.labelGroup

    private fun setupEditor() {
        setupMovementMethod()

        binding.etBody.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.bold -> {
                        applySpan(StyleSpan(Typeface.BOLD))
                        mode?.finish()
                    }
                    R.id.link -> {
                        applySpan(URLSpan(null))
                        mode?.finish()
                    }
                    R.id.italic -> {
                        applySpan(StyleSpan(Typeface.ITALIC))
                        mode?.finish()
                    }
                    R.id.monospace -> {
                        applySpan(TypefaceSpan("monospace"))
                        mode?.finish()
                    }
                    R.id.strikethrough -> {
                        applySpan(StrikethroughSpan())
                        mode?.finish()
                    }
                    R.id.clearFormatting -> {
                        removeSpans()
                        mode?.finish()
                    }
                }
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                binding.etBody.isActionModeOn = true
                mode?.menuInflater?.inflate(R.menu.formatting, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

            override fun onDestroyActionMode(mode: ActionMode?) {
                binding.etBody.isActionModeOn = false
            }
        }
    }

    private fun setupListeners() {
        binding.etTitle.doAfterTextChanged { text ->
            model.title = text.toString().trim()
        }
        binding.etBody.doAfterTextChanged { text ->
            model.body = text
        }
    }

    private fun setStateFromModel() {
        val formatter = BaseNoteVM.getDateFormatter(getLocale())

        binding.etTitle.setText(model.title)
        binding.etBody.text = model.body
        binding.tvDateCreated.text = formatter.format(model.timestamp)

        binding.labelGroup.bindLabels(model.labels)
    }

    private fun setupTextSize() {
        val size = settingsHelper.getFontSize().toFloat()
        binding.tvDateCreated.textSize = size
        binding.etTitle.textSize = size
        binding.etBody.textSize = size
        binding.tvList.textSize = size
        binding.addItem.textSize = size
    }

    private fun setupMovementMethod() {
        val movementMethod = LinkMovementMethod { span ->
            MaterialAlertDialogBuilder(this)
                .setItems(R.array.linkOptions) { _, which ->
                    if (which == 1) {
                        val spanStart = binding.etBody.text?.getSpanStart(span)
                        val spanEnd = binding.etBody.text?.getSpanEnd(span)

                        ifBothNotNullAndInvalid(spanStart, spanEnd) { start, end ->
                            val text = binding.etBody.text?.substring(start, end)
                            if (text != null) {
                                val link = getURLFrom(text)
                                val uri = Uri.parse(link)

                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    startActivity(intent)
                                } catch (exception: Exception) {
                                    Toast.makeText(this, R.string.cant_open_link, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }.show()
        }
        binding.etBody.movementMethod = movementMethod
    }

    private fun removeSpans() {
        val selectionEnd = binding.etBody.selectionEnd
        val selectionStart = binding.etBody.selectionStart

        ifBothNotNullAndInvalid(selectionStart, selectionEnd) { start, end ->
            binding.etBody.text?.getSpans(start, end, CharacterStyle::class.java)?.forEach { span ->
                binding.etBody.text?.removeSpan(span)
            }
        }
    }

    private fun applySpan(spanToApply: Any) {
        val selectionEnd = binding.etBody.selectionEnd
        val selectionStart = binding.etBody.selectionStart

        ifBothNotNullAndInvalid(selectionStart, selectionEnd) { start, end ->
            binding.etBody.text?.setSpan(spanToApply, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun ifBothNotNullAndInvalid(start: Int?, end: Int?, function: (start: Int, end: Int) -> Unit) {
        if (start != null && start != -1 && end != null && end != -1) {
            function.invoke(start, end)
        }
    }

    companion object {

        fun getURLFrom(text: String): String {
            return when {
                text.matches(Patterns.PHONE.toRegex()) -> "tel:$text"
                text.matches(Patterns.EMAIL_ADDRESS.toRegex()) -> "mailto:$text"
                text.matches(Patterns.DOMAIN_NAME.toRegex()) -> "http://$text"
                else -> text
            }
        }
    }
}
