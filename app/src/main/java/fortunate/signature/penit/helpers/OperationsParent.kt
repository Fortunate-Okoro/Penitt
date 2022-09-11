package fortunate.signature.penit.helpers

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.databinding.DialogInputBinding
import fortunate.signature.penit.model.Label

interface OperationsParent {

    fun accessContext(): Context

    fun insertLabel(label: Label, onComplete: (success: Boolean) -> Unit)

    fun labelNote(labels: List<String>, currentLabels: HashSet<String>, onUpdated: (labels: HashSet<String>) -> Unit) {
        val checkedPositions = BooleanArray(labels.size) { index ->
            val label = labels[index]
            currentLabels.contains(label)
        }

        val builder = MaterialAlertDialogBuilder(accessContext())
            .setTitle(R.string.library)
            .setNegativeButton(R.string.cancel, null)

        if (labels.isNotEmpty()) {
            builder.setMultiChoiceItems(labels.toTypedArray(), checkedPositions) { _, which, isChecked ->
                checkedPositions[which] = isChecked
            }
            builder.setPositiveButton(R.string.save) { _, _ ->
                val selectedLabels = HashSet<String>()
                checkedPositions.forEachIndexed { index, checked ->
                    if (checked) {
                        val label = labels[index]
                        selectedLabels.add(label)
                    }
                }
                onUpdated(selectedLabels)
            }
        }
        else {
            builder.setMessage(R.string.create_new)
//            val dialog = builder.create()
            builder.setPositiveButton(R.string.create_one) { dialog, _ ->
                dialog.dismiss()
                displayAddLabelDialog(currentLabels, onUpdated)
            }
        }
        builder.show()
    }

    private fun displayAddLabelDialog(currentLabels: HashSet<String>, onUpdated: (labels: HashSet<String>) -> Unit) {
        val inflater = LayoutInflater.from(accessContext())
        val binding = DialogInputBinding.inflate(inflater)

        MaterialAlertDialogBuilder(accessContext())
            .setTitle(R.string.add_library)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val value = binding.EditText.text.toString().trim()
                if (value.isEmpty()) {
                    dialog.dismiss()
                } else {
                    val label = Label(value)
                    insertLabel(label) { success ->
                        if (success) {
                            dialog.dismiss()
                            labelNote(listOf(value), currentLabels, onUpdated)
                        } else binding.root.error = accessContext().getString(R.string.library_exists)
                    }
                }
            }
            .show()

        binding.EditText.requestFocus()
    }
}