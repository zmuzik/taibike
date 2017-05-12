package zmuzik.ubike

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import zmuzik.ubike.model.Station

class StationsListAdapter(private val values: List<Station>, val location: Location) :
        RecyclerView.Adapter<StationsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_station_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stationName.text = values[position].nameEn
        holder.description.text = values[position].descriptionEn
        holder.bikesPresent.text = values[position].presentBikes.toString()
        holder.parkingSpots.text = "P " + values[position].parkingSpots.toString()
        holder.distance.text = getFormattedDistance(values[position].getDistanceFrom(location))
        holder.itemRoot.setOnClickListener {
            holder.description.visibility =
                    if (holder.description.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    fun getFormattedDistance(dist: Double): String {
        if (dist < 1) {
            return "%.0f m".format(dist * 1000)
        } else if (dist < 10) {
            return "%.2f km".format(dist)
        } else {
            return "%.0f km".format(dist)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val itemRoot: View) : RecyclerView.ViewHolder(itemRoot) {
        val stationName: TextView = itemRoot.findViewById(R.id.stationName) as TextView
        val description: TextView = itemRoot.findViewById(R.id.description) as TextView
        val bikesPresent: TextView = itemRoot.findViewById(R.id.bikesPresent) as TextView
        val parkingSpots: TextView = itemRoot.findViewById(R.id.parkingSpots) as TextView
        val distance: TextView = itemRoot.findViewById(R.id.distance) as TextView
    }
}
