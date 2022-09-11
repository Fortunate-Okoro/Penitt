package fortunate.signature.penit.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.BaseNoteVM
import fortunate.signature.penit.databinding.FragmentNoteBaseBinding
import fortunate.signature.penit.dialog.MenuDialog
import fortunate.signature.penit.entities.Folder
import fortunate.signature.penit.helpers.*
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.model.*
import fortunate.signature.penit.ui.activities.NoteActivity
import fortunate.signature.penit.ui.adapter.BaseNoteAdapter
import fortunate.signature.penit.ui.adapter.viewholder.ItemListener
import kotlinx.coroutines.launch

abstract class NoteBaseFragment : Fragment(), OperationsParent, ItemListener {

    private lateinit var settingsHelper: SettingsHelper

    private var adapter: BaseNoteAdapter? = null
    private var binding: FragmentNoteBaseBinding? = null

    internal val model: BaseNoteVM by activityViewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsHelper = SettingsHelper(requireContext())

        adapter = BaseNoteAdapter(settingsHelper, model.formatter, this)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (itemCount > 0) {
                    binding?.rvNotes?.scrollToPosition(positionStart)
                }
            }
        })
        binding?.rvNotes?.adapter = adapter
        binding?.rvNotes?.setHasFixedSize(true)

        binding?.ivBackground?.setImageResource(getBackground())
        binding?.tvBackground?.setText(getEmptyMessage())

        setupRecyclerView()
        setupObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNoteBaseBinding.inflate(inflater)
        return binding?.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RequestCodeExportFile && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                model.writeCurrentFileToUri(uri)
            }
        }
    }

    override fun onClick(position: Int) {
        adapter?.currentList?.get(position)?.let { item ->
            if (item is BaseNote) {
                goToActivity(NoteActivity::class.java, item)
            }
        }
    }

    override fun onLongClick(position: Int) {
        adapter?.currentList?.get(position)?.let { item ->
            if (item is BaseNote) {
                showOperations(item)
            }
        }
    }

    override fun accessContext(): Context {
        return requireContext()
    }

    override fun insertLabel(label: Label, onComplete: (success: Boolean) -> Unit) {
        model.insertLabel(label, onComplete)
    }

    private fun setupObserver() {
        getObservable().observe(viewLifecycleOwner) { list ->
            adapter?.submitList(list)
            binding?.rvNotes?.isVisible = list.isNotEmpty()
        }
    }

    private fun setupRecyclerView() {
        binding?.rvNotes?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showOperations(baseNote: BaseNote) {
        val dialog = MenuDialog(requireContext())
        when (baseNote.folder) {
            Folder.NOTES -> {
                dialog.dialogTitle(R.string.note_options)
                dialog.add(R.string.archive, R.drawable.ic_archive) { model.moveBaseNoteToArchive(baseNote.id) }
                if (baseNote.pinned) {
                    dialog.add(R.string.unpin, R.drawable.ic_unpin) { model.unpinBaseNote(baseNote.id) }
                } else dialog.add(R.string.pin, R.drawable.ic_pin) { model.pinBaseNote(baseNote.id) }
                dialog.add(R.string.share, R.drawable.ic_share) { share(baseNote) }
                dialog.add(R.string.clipboard, R.drawable.ic_clipboard) { clipboardCopy(baseNote) }
                dialog.add(R.string.library_add, R.drawable.ic_library_notes) { label(baseNote) }
                dialog.add(R.string.delete, R.drawable.ic_delete) { model.moveBaseNoteToDeleted(baseNote.id) }
            }
            Folder.DELETED -> {
                dialog.dialogTitle(R.string.delete_options)
                dialog.add(R.string.restore, R.drawable.ic_restore) { model.restoreBaseNote(baseNote.id) }
                dialog.add(R.string.delete_forever, R.drawable.ic_delete) { delete(baseNote) }
            }
            Folder.ARCHIVED -> {
                dialog.dialogTitle(R.string.archive_options)
                dialog.add(R.string.unarchive, R.drawable.ic_unarchive) { model.restoreBaseNote(baseNote.id) }
            }
        }
        dialog.show()
    }

    private fun clipboardCopy(baseNote: BaseNote) {
        val body = baseNote.body.applySpans(baseNote.spans)
        val list = baseNote.items.getBody()
        val view = "$body \n $list"
        Operations.shareNote(requireContext(), baseNote.title, view)
    }

    internal fun goToActivity(activity: Class<*>, baseNote: BaseNote? = null) {
        val intent = Intent(requireContext(), activity)
        intent.putExtra(Constants.SelectedBaseNote, baseNote)
        startActivity(intent)
    }

    private fun share(baseNote: BaseNote) {
        val body = baseNote.body.applySpans(baseNote.spans)
        val list = baseNote.items.getBody()
        val view = "$body \n $list"
        Operations.shareNote(requireContext(), baseNote.title, view)
    }

    private fun label(baseNote: BaseNote) {
        lifecycleScope.launch {
            val labels = model.getAllLabelsAsList()
            labelNote(labels, baseNote.labels) { updatedLabels ->
                model.updateBaseNoteLabels(updatedLabels, baseNote.id)
            }
        }
    }

    private fun delete(baseNote: BaseNote) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_note_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                model.deleteBaseNoteForever(baseNote)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    abstract fun getBackground(): Int

    abstract fun getEmptyMessage(): Int

    abstract fun getObservable(): LiveData<List<Item>>
}
