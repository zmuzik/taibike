package zmuzik.taibike.screens.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.ext.android.inject
import zmuzik.taibike.Conf
import zmuzik.taibike.R
import zmuzik.taibike.common.geoDistance
import zmuzik.taibike.common.getFormattedDistance
import zmuzik.taibike.common.isLocationPermissionGranted
import zmuzik.taibike.repo.entity.Station


class StationsMapFragment : Fragment(), GoogleMap.InfoWindowAdapter {

    val viewModel: StationsMapViewModel by inject()

    var stations = mutableListOf<Station>()
    var markers = mutableListOf<Marker>()
    var currentLocation: LatLng? = null

    var googleMap: GoogleMap? = null
    var isZoomedInPosition: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getAllStations().observe(this, Observer { it?.let { onItemsLoaded(it) } })
        viewModel.showStationOnMapEvent.observe(this, Observer { it?.let { onShowStationOnMapRequested(it) } })
        viewModel.location.observe(this, Observer { it?.let { onLocationUpdated(it) } })
    }

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
            viewModel.location.value?.let {
                val cameraPosition = CameraPosition.builder().target(it).zoom(13f).build()
                googleMap.let { googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)) }
            }
            maybeEnableLocationButton()

            googleMap?.resetMinMaxZoomPreference()
            if (stations.isNotEmpty() && activity != null && isAdded) setMarkersToMap(stations)
            mapView?.width
        }
    }

    fun onItemsLoaded(list: List<Station>) {
        stations.clear()
        stations.addAll(list)
        updateListAndMap()
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
