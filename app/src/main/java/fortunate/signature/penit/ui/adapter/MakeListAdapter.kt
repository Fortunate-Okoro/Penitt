package fortunate.signature.penit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fortunate.signature.penit.databinding.NoteListItemBinding
import fortunate.signature.penit.helpers.SettingsHelper
import fortunate.signature.penit.model.ListItem
import fortunate.signature.penit.ui.adapter.viewholder.ListItemListener
import fortunate.signature.penit.ui.adapter.viewholder.MakeListVH
import java.util.ArrayList

class MakeListAdapter(
    private val items: ArrayList<ListItem>,
    private val settingsHelper: SettingsHelper,
    private val listener: ListItemListener
) :
    RecyclerView.Adapter<MakeListVH>() {

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MakeListVH, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakeListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteListItemBinding.inflate(inflater, parent, false)
        return MakeListVH(binding, settingsHelper, listener)
    }
}