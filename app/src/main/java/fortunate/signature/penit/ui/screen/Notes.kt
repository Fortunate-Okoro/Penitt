package fortunate.signature.penit.ui.screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import fortunate.signature.penit.MainActivity
import fortunate.signature.penit.R
import fortunate.signature.penit.misc.add
import fortunate.signature.penit.ui.*
import fortunate.signature.penit.ui.activities.NoteActivity

class Notes : NoteBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireContext() as MainActivity).binding.btnCreateNew.setOnClickListener {
            goToActivity(NoteActivity::class.java)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(R.string.search, R.drawable.ic_search) { findNavController().navigate(R.id.NotesToSearch) }
    }

    override fun getObservable() = model.baseNotes

    override fun getEmptyMessage(): Int = R.string.no_note

    override fun getBackground() = R.drawable.ic_note_list_image
}