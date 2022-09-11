package fortunate.signature.penit.ui.screen

import androidx.lifecycle.LiveData
import fortunate.signature.penit.R
import fortunate.signature.penit.misc.Constants
import fortunate.signature.penit.model.Item
import fortunate.signature.penit.ui.NoteBaseFragment

class DisplayLabel : NoteBaseFragment() {

    override fun getBackground() = R.drawable.ic_library_image

    override fun getObservable(): LiveData<List<Item>> {
        val label = requireNotNull(requireArguments().getString(Constants.SelectedLabel))
        return model.getNotesByLabel(label)
    }

    override fun getEmptyMessage(): Int = R.string.empty_label_screen
}