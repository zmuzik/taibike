package zmuzik.taibike

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import zmuzik.taibike.bus.LocationUpdatedEvent
import zmuzik.taibike.bus.ShowStationOnMapEvent
import zmuzik.taibike.bus.StationsUpdatedEvent
import zmuzik.taibike.bus.UiBus
import zmuzik.taibike.di.ActivityScope
import zmuzik.taibike.di.DaggerMainScreenComponent
import zmuzik.taibike.di.MainScreenComponent
import zmuzik.taibike.di.MainScreenModule
import zmuzik.taibike.model.Station
import zmuzik.taibike.utils.getFormattedDistance
import java.lang.ref.WeakReference
import javax.inject.Inject


@ActivityScope
class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter {

    val INITIAL_FORCE_ZOOM_LEVEL: Float = 16f
    val CITY_ZOOM_LEVEL: Float = 13f
    val PREF_MIN_ZOOM_LEVEL: Float = 10f
    val PREF_MAX_ZOOM_LEVEL: Float = 20f
    val TAIPEI_CENTER_COORDS = LatLng(25.0410, 121.5438)

    lateinit var component: MainScreenComponent

    @Inject
    lateinit var presenter: MainScreenPresenter

    var stationList: List<Station> = ArrayList()
    var markers: ArrayList<Marker> = ArrayList()
    var map: GoogleMap? = null
    var lastLoc: Location? = null
    var isZoomedInPosition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inject()
        viewPager.adapter = PagesAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = when (position) {
                    0 -> R.id.navigation_map
                    else -> R.id.navigation_list
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }
        })
        navigation.setOnNavigationItemSelectedListener(this)
        (viewPager.adapter as PagesAdapter).getMapFr().getMapAsync(this)
    }

    private fun inject() {
        component = DaggerMainScreenComponent.builder()
                .appComponent((applicationContext as App).appComponent)
                .mainScreenModule(MainScreenModule(this))
                .build()
        component.inject(this)
        component.inject(presenter)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
        UiBus.get().register(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
        UiBus.get().unregister(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> viewPager.currentItem = 0
            R.id.navigation_list -> viewPager.currentItem = 1
        }
        return true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == presenter.REQUEST_PERMISSION_LOC) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.requestLocation()
                map?.isMyLocationEnabled = true
            }
        }
    }

    @Subscribe fun onLocationChanged(event: LocationUpdatedEvent) {
        lastLoc = event.location
        maybeUpdateLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(newMap: GoogleMap) {
        map = newMap
        newMap.setMinZoomPreference(PREF_MIN_ZOOM_LEVEL)
        newMap.setMaxZoomPreference(PREF_MAX_ZOOM_LEVEL)
        newMap.setInfoWindowAdapter(this)
        newMap.uiSettings?.isZoomControlsEnabled = true
        if (presenter.isLocPermission()) newMap.isMyLocationEnabled = true
        maybeUpdateLocation()
        maybeRedrawMarkers()
    }

    private fun maybeUpdateLocation() {
        map?.let {
            if (isZoomedInPosition) {
                // no map to zoom or already zoomed, do nothing
                return
            } else if (lastLoc == null) {
                // no precise location (yet), zoom to taipei
                it.moveCamera(CameraUpdateFactory.newLatLng(TAIPEI_CENTER_COORDS))
                it.moveCamera(CameraUpdateFactory.zoomTo(CITY_ZOOM_LEVEL))
            } else {
                // zoom to precise location
                val ll: LatLng = LatLng(lastLoc!!.latitude, lastLoc!!.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLng(ll))
                it.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_FORCE_ZOOM_LEVEL))
                isZoomedInPosition = true
            }
        }
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {
        stationList = event.list
        maybeRedrawMarkers()
    }

    @Subscribe fun onShowStationOnMapRequested(event: ShowStationOnMapEvent) {
        map?.let {
            viewPager.setCurrentItem(0, true)
            it.moveCamera(CameraUpdateFactory.newLatLng(event.station.getLatLng()))
            it.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_FORCE_ZOOM_LEVEL))
            val marker: Marker? = markers.find { it.tag == event.station.id }
            marker?.showInfoWindow()
        }
    }

    fun maybeRedrawMarkers() {
        map?.let {
            it.clear()
            markers = ArrayList<Marker>()
            for (station: Station in stationList) {
                val marker = it.addMarker(station.getMarkerOptions(this))
                marker.tag = station.id
                markers.add(marker)
            }
        }
    }

    override fun getInfoContents(marker: Marker?): View = getInfoView(marker)

    override fun getInfoWindow(marker: Marker?): View = getInfoView(marker)

    fun getInfoView(marker: Marker?): View {
        val root = layoutInflater.inflate(R.layout.info_window, null)
        val stationName: TextView = root.findViewById(R.id.stationName) as TextView
        val description: TextView = root.findViewById(R.id.description) as TextView
        val bikesPresent: TextView = root.findViewById(R.id.bikesPresent) as TextView
        val parkingSpots: TextView = root.findViewById(R.id.parkingSpots) as TextView
        val distance: TextView = root.findViewById(R.id.distance) as TextView
        val timeUpdated: TextView = root.findViewById(R.id.timeUpdated) as TextView
        val id: Int = marker?.tag as Int
        val station = stationList.find { it.id == id } ?: return root
        stationName.text = station.nameEn
        description.text = station.descriptionEn
        bikesPresent.text = station.presentBikes.toString()
        parkingSpots.text = "P " + station.parkingSpots.toString()
        timeUpdated.text = "Updated " + station.date + " CST"
        distance.text = getFormattedDistance(station.getDistanceFrom(lastLoc))
        return root
    }

    inner class PagesAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var mapFragment: WeakReference<SupportMapFragment>? = null
        var listFragment: WeakReference<StationsListFragment>? = null

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> getMapFr()
                else -> getListFr()
            }
        }

        fun getMapFr(): SupportMapFragment {
            if (mapFragment == null || mapFragment?.get() == null) {
                mapFragment = WeakReference(SupportMapFragment.newInstance())
            }
            return mapFragment!!.get()!!
        }

        fun getListFr(): StationsListFragment {
            if (listFragment == null || listFragment?.get() == null) {
                listFragment = WeakReference(StationsListFragment())
            }
            return listFragment!!.get()!!
        }
    }
}
