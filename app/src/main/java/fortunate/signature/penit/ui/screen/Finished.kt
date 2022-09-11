package fortunate.signature.penit.ui.screen

import fortunate.signature.penit.R
import fortunate.signature.penit.ui.TaskBaseFragment

class Finished : TaskBaseFragment() {

    override fun getBackground() = R.drawable.ic_archive_image

    override fun getEmptyMessage(): Int = R.string.empty_archived_screen

    override fun getObservable() = model.finishedTasks
}