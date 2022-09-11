package fortunate.signature.penit.ui.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import fortunate.signature.penit.MainActivity
import fortunate.signature.penit.R
import fortunate.signature.penit.misc.add
import fortunate.signature.penit.ui.TaskBaseFragment
import fortunate.signature.penit.ui.activities.TaskActivity

class Tasks : TaskBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireContext() as MainActivity).binding.btnCreateNew.setOnClickListener {
            goToActivity(TaskActivity::class.java)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(R.string.search, R.drawable.ic_search) { findNavController().navigate(R.id.searchTask) }
    }

    override fun getObservable() = model.tasks

    override fun getEmptyMessage(): Int = R.string.no_tasks

    override fun getBackground() = R.drawable.ic_todo_list

}