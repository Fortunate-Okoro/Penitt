package fortunate.signature.penit.model

import androidx.room.Ignore
import fortunate.signature.penit.entities.ViewType

sealed class Item(@Ignore val viewType: ViewType)