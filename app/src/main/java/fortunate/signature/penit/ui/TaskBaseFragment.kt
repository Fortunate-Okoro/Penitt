package fortunate.signature.penit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fortunate.signature.penit.R
import fortunate.signature.penit.data.viewmodel.BaseTaskVM
import fortunate.signature.penit.databinding.FragmentTaskBaseBinding
import fortunate.signature.penit.dialog.MenuDialog
import fortunate.signature.penit.entities.TaskFolder
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.Constants
import fortunate.signature.penit.model.Task
import fortunate.signature.penit.model.TaskItem
import fortunate.signature.penit.ui.activities.TaskActivity
import fortunate.signature.penit.ui.adapter.TaskAdapter
import fortunate.signature.penit.ui.adapter.viewholder.ItemListener

abstract class TaskBaseFragment : Fragment(), ItemListener {

    private lateinit var settingsHelper: SettingsHelper

    private var adapter: TaskAdapter? = null
    private var binding: FragmentTaskBaseBinding? = null

    internal val model : BaseTaskVM by activityViewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsHelper = SettingsHelper(requireContext())

        adapter = TaskAdapter(this)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (itemCount > 0) {
                    binding?.rvTasks?.scrollToPosition(positionStart)
                }
            }
        })
        binding?.rvTasks?.adapter = adapter
        binding?.rvTasks?.setHasFixedSize(true)

        binding?.ivBackground?.setImageResource(getBackground())
        binding?.tvBackground?.setText(getEmptyMessage())

        setupRecyclerView()
        setupObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBaseBinding.inflate(inflater)
        return binding?.root!!
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
            if (item is Task) {
                goToActivity(TaskActivity::class.java, item)
            }
        }
    }

    override fun onLongClick(position: Int) {
        adapter?.currentList?.get(position)?.let { item ->
            if (item is Task) {
                showOperations(item)
            }
        }
    }

    private fun setupObserver() {
        getObservable().observe(viewLifecycleOwner) { list ->
            adapter?.submitList(list)
            binding?.rvTasks?.isVisible = list.isNotEmpty()
        }
    }

    private fun setupRecyclerView() {
        binding?.rvTasks?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showOperations(task: Task) {
        val dialog = MenuDialog(requireContext())
        when (task.folder) {
            TaskFolder.TASKS-> {
                dialog.dialogTitle(R.string.note_options)
                dialog.add(R.string.archive, R.drawable.ic_archive) { model.moveTaskToArchive(task.id) }
                if (task.due) {
                    dialog.add(R.string.unpin, R.drawable.ic_unpin) { model.undueTask(task.id) }
                } else dialog.add(R.string.pin, R.drawable.ic_pin) { model.dueTask(task.id) }
                dialog.add(R.string.delete, R.drawable.ic_delete) { model.moveTaskToDeleted(task.id) }
            }
            TaskFolder.DELETED -> {
                dialog.dialogTitle(R.string.delete_options)
                dialog.add(R.string.restore, R.drawable.ic_restore) { model.restoreTask(task.id) }
                dialog.add(R.string.delete_forever, R.drawable.ic_delete) { delete(task) }
            }
            TaskFolder.FINISHED -> {
                dialog.dialogTitle(R.string.archive_options)
                dialog.add(R.string.unarchive, R.drawable.ic_unarchive) { model.restoreTask(task.id) }
            }
        }
        dialog.show()
    }

    internal fun goToActivity(activity: Class<*>, task: Task? = null) {
        val intent = Intent(requireContext(), activity)
        intent.putExtra(Constants.SelectedTask, task)
        startActivity(intent)
    }

    private fun delete(task: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_note_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                model.deleteTaskForever(task)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    abstract fun getBackground(): Int

    abstract fun getEmptyMessage(): Int

    abstract fun getObservable(): LiveData<List<TaskItem>>
}