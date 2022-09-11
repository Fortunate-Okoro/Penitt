package fortunate.signature.penit.ui.screen

import fortunate.signature.penit.R
import fortunate.signature.penit.ui.NoteBaseFragment

class Search : NoteBaseFragment() {

    override fun getBackground() = R.drawable.ic_search

    override fun getEmptyMessage(): Int = R.string.empty_search_screen

    override fun getObservable() = model.searchResults
}