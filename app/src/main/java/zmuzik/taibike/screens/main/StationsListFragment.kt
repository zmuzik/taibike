package zmuzik.taibike.screens.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_station_list.*
import kotlinx.android.synthetic.main.item_station_list.view.*
import org.koin.android.architecture.ext.sharedViewModel
import zmuzik.taibike.R
import zmuzik.taibike.common.*
import zmuzik.taibike.repo.ApiResource
import zmuzik.taibike.repo.entity.Station


class StationsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    val viewModel: MainScreenViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_station_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayout.VERTICAL))
            adapter = StationsListAdapter().also { it.location = viewModel.locationLd.value }
        }
        progressBar.setColor(R.color.primary_dark)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.primary)
        viewModel.locationLd.observe(this, Observer { it?.let { (recyclerView.adapter as StationsListAdapter).updateLocation(it) } })
        viewModel.stationsLd.observe(this, Observer { it?.let { (recyclerView.adapter as StationsListAdapter).onStationsUpdated(it) } })
    }

    override fun onRefresh() {
        viewModel.refreshStations(true)
    }

    inner class StationsListAdapter : RecyclerView.Adapter<StationsListAdapter.ViewHolder>() {

        val stations = mutableListOf<Station>()
        var location: LatLng? = null

        fun updateLocation(newLocation: LatLng) {
            location = newLocation
            stations.sortBy { geoDistance(newLocation.latitude, newLocation.longitude, it.lat, it.lng) }
            notifyDataSetChanged()
        }

        fun onStationsUpdated(apiResource: ApiResource<List<Station>>) = when (apiResource) {
            is ApiResource.Loading -> {
                progressBar.show()
            }
            is ApiResource.Success -> {
                progressBar.hide()
                swipeRefreshLayout.isRefreshing = false
                stations.clear()
                apiResource.data?.let { stations.addAll(it) }
                location?.let { loc -> stations.sortBy { geoDistance(loc.latitude, loc.longitude, it.lat, it.lng) } }
                notifyDataSetChanged()
            }
            is ApiResource.Failure -> {
                progressBar.hide()
                swipeRefreshLayout.isRefreshing = false
                activity?.toast(R.string.error_loading_data)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_station_list, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindItem()

        fun toggleVisibility(view: View) = view.setVisibility(if (view.visibility == View.GONE) View.VISIBLE else View.GONE)

        override fun getItemCount(): Int = stations.size

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bindItem() {
                val station = stations[position]
                itemView.stationName.text = station.nameEn
                itemView.description.text = station.descriptionEn
                itemView.bikesPresent.text = station.presentBikes.toString()
                itemView.parkingSpots.text = "P " + station.parkingSpots.toString()
                itemView.distance.text = getFormattedDistance(station.getDistanceFrom(location))
                itemView.timeUpdated.text = "Updated " + station.date + " CST"

                itemView.setOnClickListener {
                    toggleVisibility(itemView.description)
                    toggleVisibility(itemView.timeUpdated)
                    toggleVisibility(itemView.map)
                }
                itemView.description.visibility = View.GONE
                itemView.timeUpdated.visibility = View.GONE
                itemView.map.visibility = View.GONE

                itemView.map.setOnClickListener { viewModel.showStationOnMap(station.id) }
            }
        }
    }
}
