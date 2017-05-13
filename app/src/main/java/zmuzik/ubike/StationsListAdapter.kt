package zmuzik.ubike

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import zmuzik.ubike.model.Station
import zmuzik.ubike.utils.getFormattedDistance

class StationsListAdapter(private val values: List<Station>, val location: Location,
                          val presenter: MainScreenPresenter) :
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
        holder.timeUpdated.text = "Updated " + values[position].date + " CST"

        holder.itemRoot.setOnClickListener {
            toggleVisibility(holder.description)
            toggleVisibility(holder.timeUpdated)
            toggleVisibility(holder.map)
        }
        holder.description.visibility = View.GONE
        holder.timeUpdated.visibility = View.GONE
        holder.map.visibility = View.GONE

        holder.map.setOnClickListener { presenter.showStationOnMap(values[position]) }
    }

    fun toggleVisibility(view: View) {
        view.visibility = if (view.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val itemRoot: View) : RecyclerView.ViewHolder(itemRoot) {
        val stationName: TextView = itemRoot.findViewById(R.id.stationName) as TextView
        val description: TextView = itemRoot.findViewById(R.id.description) as TextView
        val bikesPresent: TextView = itemRoot.findViewById(R.id.bikesPresent) as TextView
        val parkingSpots: TextView = itemRoot.findViewById(R.id.parkingSpots) as TextView
        val distance: TextView = itemRoot.findViewById(R.id.distance) as TextView
        val timeUpdated: TextView = itemRoot.findViewById(R.id.timeUpdated) as TextView
        val map: ImageView = itemRoot.findViewById(R.id.map) as ImageView
    }
}
