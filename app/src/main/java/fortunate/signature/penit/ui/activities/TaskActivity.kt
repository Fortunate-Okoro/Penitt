package fortunate.signature.penit.ui.activities

import android.os.Bundle
import android.text.TextUtils
import androidx.core.widget.doAfterTextChanged
import fortunate.signature.penit.data.viewmodel.BaseTaskVM
import fortunate.signature.penit.databinding.ActivityTaskBinding
import fortunate.signature.penit.dialog.DatePickerDialog
import fortunate.signature.penit.entities.Priority
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.misc.*
import fortunate.signature.penit.ui.TaskBaseActivity

class TaskActivity : TaskBaseActivity() {
    private lateinit var settingsHelper: SettingsHelper

    override val binding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsHelper = SettingsHelper(this)

        binding.etTaskName.setOnNextAction {
            binding.etTaskDescription.requestFocus()
        }

        setupToolbar(binding.toolbar)
        supportActionBar?.title = "Create a task"

        if (model.isNewTask) {
            binding.etTaskName.requestFocus()
        }

        setStateFromModel()
        setupListeners()
        setFontSize()
    }

    private fun checkData(title: String, description: String, dateCreated: String, dueDate: String): Boolean {
        return if ((TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(dateCreated) || TextUtils.isEmpty(dueDate))) {
            false
        } else !(title.isEmpty() || description.isEmpty())
    }

    private fun setupListeners() {
        binding.etTaskName.doAfterTextChanged { text ->
            model.title = text.toString().trim()
        }
        binding.etTaskDescription.doAfterTextChanged { text ->
            model.description = text.toString().trim()
        }
        binding.tvDueDate.setOnClickListener {
            showDatePicker()
        }
        binding.tvDueDate.doAfterTextChanged { text ->
            model.dueDate = text.toString().trim()
        }
        when (model.priority) {
            Priority.NONE -> binding.priorityOptions.setSelection(0)
            Priority.IMPORTANT -> binding.priorityOptions.setSelection(1)
            Priority.BASIC -> binding.priorityOptions.setSelection(2)
            Priority.LOW -> binding.priorityOptions.setSelection(3)
        }

    }

    private fun setStateFromModel() {
        val formatter = BaseTaskVM.getDateFormatter(getLocale())

        binding.tvDateCreated.text = formatter.format(model.timestamp)
        binding.etTaskName.setText(model.title)
        binding.etTaskDescription.setText(model.description)
        binding.tvDueDate.text = model.dueDate
    }

    private fun setFontSize() {
        val size = settingsHelper.getFontSize().toFloat()
        binding.etTaskName.textSize = size
        binding.tvPriority.textSize = size
        binding.etTaskDescription.textSize = size
        binding.tvDate.textSize = size
        binding.tvDateCreated.textSize = size
        binding.tvDueDate.textSize = size
    }

    private fun showDatePicker() {
        val datePickerFragment = DatePickerDialog { dueDate ->
            binding.tvDueDate.text = dueDate
        }
        datePickerFragment.show(supportFragmentManager, "datePicker")
    }

}
