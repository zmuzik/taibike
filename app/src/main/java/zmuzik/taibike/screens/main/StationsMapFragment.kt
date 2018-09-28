package zmuzik.taibike.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import zmuzik.taibike.Conf
import zmuzik.taibike.R
import zmuzik.taibike.common.*
import zmuzik.taibike.repo.ApiResource
import zmuzik.taibike.repo.entity.Station


class StationsMapFragment : Fragment(), GoogleMap.InfoWindowAdapter {

    val viewModel: MainScreenViewModel by sharedViewModel()

    var stations = mutableListOf<Station>()
    var markers = mutableListOf<Marker>()
    var currentLocation: LatLng? = null

    var googleMap: GoogleMap? = null
    var isZoomedInPosition: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap?.clear()
            googleMap?.setMinZoomPreference(Conf.PREF_MIN_ZOOM_LEVEL)
            googleMap?.setMaxZoomPreference(Conf.PREF_MAX_ZOOM_LEVEL)
            googleMap?.setInfoWindowAdapter(this)
            googleMap?.uiSettings?.isZoomControlsEnabled = true
            viewModel.locationLd.value?.let {
                val cameraPosition = CameraPosition.builder().target(it).zoom(13f).build()
                googleMap.let { googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)) }
            }
            maybeEnableLocationButton()

            googleMap?.resetMinMaxZoomPreference()
            if (stations.isNotEmpty() && activity != null && isAdded) setMarkersToMap(stations)
            mapView?.width
        }
        progressBar.setColor(R.color.primary_dark)
        viewModel.showStationOnMapEvent.observe(this, Observer { it?.let { onShowStationOnMapRequested(it) } })
        viewModel.locationLd.observe(this, Observer { it?.let { onLocationUpdated(it) } })
        viewModel.stationsLd.observe(this, Observer { it?.let { onStationsUpdated(it) } })
    }

    fun onStationsUpdated(apiResource: ApiResource<List<Station>>) = when (apiResource) {
        is ApiResource.Loading -> {
            progressBar.show()
        }
        is ApiResource.Success -> {
            progressBar.hide()
            stations.clear()
            apiResource.data?.let { stations.addAll(it) }
            updateListAndMap()
        }
        is ApiResource.Failure -> {
            progressBar.hide()
            activity?.toast(R.string.error_loading_data)
        }
    }

    fun onLocationUpdated(newLocation: LatLng) {
        currentLocation = newLocation
        when {
            isZoomedInPosition -> return
            currentLocation == null -> moveMapTo(Conf.TAIPEI_CENTER_COORDS, Conf.CITY_ZOOM_LEVEL)
            else -> currentLocation?.let { loc ->
                if (geoDistance(loc, Conf.TAIPEI_CENTER_COORDS) > Conf.MAX_DISTANCE_FROM_TAIPEI) {
                    moveMapTo(Conf.TAIPEI_CENTER_COORDS, Conf.CITY_ZOOM_LEVEL)
                } else {
                    moveMapTo(loc, Conf.INITIAL_FORCE_ZOOM_LEVEL)
                }
                isZoomedInPosition = true
            }
        }
        updateListAndMap()
    }

    fun moveMapTo(position: LatLng, zoom: Float) = googleMap?.moveCamera(CameraUpdateFactory
            .newCameraPosition(CameraPosition.Builder().target(position).zoom(zoom).build()))

    fun updateListAndMap() {
        stations.sortBy { it.getDistanceFrom(currentLocation) }
        googleMap?.let { setMarkersToMap(stations) }
    }

    fun setMarkersToMap(list: List<Station>) {
        val map = googleMap ?: return
        val ctx = context ?: return
        map.clear()
        markers.clear()
        for (item in list) {
            val marker = map.addMarker(item.getMarkerOptions(ctx)).also { it.tag = item.id }
            markers.add(marker)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        maybeEnableLocationButton()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mapView.onDestroy()
        } catch (e: Exception) {
            // intentionally left blank
        }
    }

    fun maybeEnableLocationButton() {
        if (context == null || googleMap == null) return
        try {
            if (googleMap?.isMyLocationEnabled != true && activity?.isLocationPermissionGranted() == true) {
                googleMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
        }
    }

    fun onShowStationOnMapRequested(stationId: Int) {
        googleMap?.let {
            val station = stations.find { it.id == stationId } ?: return@let
            moveMapTo(station.getLocation(), Conf.INITIAL_FORCE_ZOOM_LEVEL)
            val marker: Marker? = markers.find { it.tag == stationId }
            marker?.showInfoWindow()
        }
    }

    override fun getInfoContents(marker: Marker?): View = getInfoView(marker)

    override fun getInfoWindow(marker: Marker?): View = getInfoView(marker)

    private fun getInfoView(marker: Marker?): View {
        val root = layoutInflater.inflate(R.layout.info_window, null)
        val stationName = root.findViewById(R.id.stationName) as TextView
        val description = root.findViewById(R.id.description) as TextView
        val bikesPresent = root.findViewById(R.id.bikesPresent) as TextView
        val parkingSpots = root.findViewById(R.id.parkingSpots) as TextView
        val distance = root.findViewById(R.id.distance) as TextView
        val timeUpdated = root.findViewById(R.id.timeUpdated) as TextView
        val id: Int = marker?.tag as Int
        val station = stations.find { it.id == id } ?: return root
        stationName.text = station.nameEn
        description.text = station.descriptionEn
        bikesPresent.text = station.presentBikes.toString()
        parkingSpots.text = "P ${station.parkingSpots}"
        timeUpdated.text = "Updated ${station.date} CST"
        distance.text = getFormattedDistance(station.getDistanceFrom(currentLocation))
        return root
    }
}
