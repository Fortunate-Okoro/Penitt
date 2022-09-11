package fortunate.signature.penit.ui.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.misc.add
import fortunate.signature.penit.ui.TaskBaseFragment

class DeletedTask : TaskBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(R.string.delete_all, R.drawable.ic_delete_all) { deleteAllTasks() }
    }

    private fun deleteAllTasks() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_all_tasks)
            .setPositiveButton(R.string.delete_task) { _, _ ->
                model.deleteAllTasks()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun getBackground() = R.drawable.ic_delete_image

    override fun getEmptyMessage(): Int = R.string.empty_deleted_screen

    override fun getObservable() = model.deletedTasks
}