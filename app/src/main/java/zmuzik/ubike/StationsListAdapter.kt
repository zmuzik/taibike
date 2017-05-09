package zmuzik.ubike

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import zmuzik.ubike.model.Station

class StationsListAdapter(private val values: List<Station>) :
        RecyclerView.Adapter<StationsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_station_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mIdView.text = values[position].id.toString()
        holder.mContentView.text = values[position].nameEn
        holder.itemRoot.setOnClickListener { }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val itemRoot: View) : RecyclerView.ViewHolder(itemRoot) {
        val mIdView: TextView = itemRoot.findViewById(R.id.id) as TextView
        val mContentView: TextView = itemRoot.findViewById(R.id.content) as TextView
    }
}
