package fortunate.signature.penit.ui.screen

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.BaseNoteVM
import fortunate.signature.penit.databinding.DialogInputBinding
import fortunate.signature.penit.databinding.FragmentNoteBaseBinding
import fortunate.signature.penit.dialog.MenuDialog
import fortunate.signature.penit.misc.Constants
import fortunate.signature.penit.model.Label
import fortunate.signature.penit.ui.adapter.LabelAdapter
import fortunate.signature.penit.ui.adapter.viewholder.ItemListener

class Labels : Fragment(), ItemListener {

    private var adapter: LabelAdapter? = null
    private var binding: FragmentNoteBaseBinding? = null

    private val model: BaseNoteVM by activityViewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = LabelAdapter(this)

        binding?.rvNotes?.setHasFixedSize(true)
        binding?.rvNotes?.adapter = adapter
        binding?.rvNotes?.layoutManager = GridLayoutManager(requireContext(), 2)
        val itemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding?.rvNotes?.addItemDecoration(itemDecoration)

        binding?.rvNotes?.setPadding(0, 0, 0, 0)
        binding?.ivBackground?.setImageResource(R.drawable.ic_library_image)

        (requireContext() as fortunate.signature.penit.MainActivity).binding.btnCreateNew.setOnClickListener {
            displayAddLabelDialog()
        }
        (requireContext() as fortunate.signature.penit.MainActivity).binding.btnCreateNew.setImageResource(R.drawable.ic_add)
        setupObserver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentNoteBaseBinding.inflate(inflater)
        return binding?.root
    }

    override fun onClick(position: Int) {
        adapter?.currentList?.get(position)?.let { value ->
            val bundle = Bundle()
            bundle.putString(Constants.SelectedLabel, value)
            findNavController().navigate(R.id.LabelsToDisplayLabel, bundle)
        }
    }

    override fun onLongClick(position: Int) {
        adapter?.currentList?.get(position)?.let { value ->
            MenuDialog(requireContext()).dialogTitle(R.string.save)
            MenuDialog(requireContext())
                .add(R.string.edit) { displayEditLabelDialog(value) }
                .add(R.string.delete_library) { confirmDeletion(value) }
                .show()
        }
    }

    private fun setupObserver() {
        model.labels.observe(viewLifecycleOwner) { labels ->
            adapter?.submitList(labels)
            binding?.rvNotes?.isVisible = labels.isNotEmpty()
        }
    }

    private fun displayAddLabelDialog() {
        val dialogBinding = DialogInputBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setTitle(R.string.add_library)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val value = dialogBinding.EditText.text.toString().trim()
                if (value.isNotEmpty()) {
                    val label = Label(value)
                    model.insertLabel(label) { success ->
                        if (success) {
                            dialog.dismiss()
                        } else dialogBinding.root.error = getString(R.string.library_exists)
                    }
                } else dialog.dismiss()
            }
            .show()

        dialogBinding.EditText.requestFocus()
    }

    private fun confirmDeletion(value: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_library)
            .setMessage(R.string.your_notes_associated)
            .setPositiveButton(R.string.delete_label) { _, _ ->
                model.deleteLabel(value)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun displayEditLabelDialog(oldValue: String) {
        val dialogBinding = DialogInputBinding.inflate(layoutInflater)

        dialogBinding.EditText.setText(oldValue)

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setTitle(R.string.edit_library)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val value = dialogBinding.EditText.text.toString().trim()
                if (value.isNotEmpty()) {
                    model.updateLabel(oldValue, value) { success ->
                        if (success) {
                            dialog.dismiss()
                        } else dialogBinding.root.error = getString(R.string.library_exists)
                    }
                } else dialog.dismiss()
            }
            .show()

        dialogBinding.EditText.requestFocus()
    }
}
