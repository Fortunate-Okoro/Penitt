package fortunate.signature.penit.data.livedata

import androidx.lifecycle.LiveData
import fortunate.signature.penit.model.*

class Content(liveData: LiveData<List<BaseNote>>, transform: (List<BaseNote>) -> List<Item>) : LiveData<List<Item>>() {

    init {
        liveData.observeForever { list -> value = transform(list) }
    }
}