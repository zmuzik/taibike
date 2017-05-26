package zmuzik.taibike

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
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
import javax.inject.Inject


@ActivityScope
class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter {

    @Inject
    lateinit var presenter: MainScreenPresenter

    lateinit var component: MainScreenComponent

    val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
    val listFragment: StationsListFragment = StationsListFragment()

    val INITIAL_FORCE_ZOOM_LEVEL: Float = 16f
    val PREF_MIN_ZOOM_LEVEL: Float = 10f
    val PREF_MAX_ZOOM_LEVEL: Float = 20f

    var stationList: List<Station>? = null
    var map: GoogleMap? = null
    var lastLoc: Location? = null
    var isZoomedInPosition: Boolean = false
    lateinit var markers: ArrayList<Marker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(R.layout.activity_main)
        mapFragment.getMapAsync(this)
        navigation.setOnNavigationItemSelectedListener(this)
        viewPager.adapter = PagesAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> navigation.selectedItemId = R.id.navigation_map
                    1 -> navigation.selectedItemId = R.id.navigation_list
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }
        })
    }

    private fun inject() {
        component = DaggerMainScreenComponent.builder()
                .appComponent((applicationContext as App).appComponent)
                .mainScreenModule(MainScreenModule(this))
                .build()
        component.inject(this)
        component.inject(presenter)
        component.inject(listFragment)
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
    override fun onMapReady(newMap: GoogleMap?) {
        map = newMap
        map?.setMinZoomPreference(PREF_MIN_ZOOM_LEVEL)
        map?.setMaxZoomPreference(PREF_MAX_ZOOM_LEVEL)
        map?.setInfoWindowAdapter(this)
        map?.uiSettings?.isZoomControlsEnabled = true
        if (presenter.isLocPermission()) map?.isMyLocationEnabled = true
        maybeUpdateLocation()
        maybeRedrawMarkers()
    }

    private fun maybeUpdateLocation() {
        if (map != null && lastLoc != null) {
            val ll: LatLng = LatLng(lastLoc!!.latitude, lastLoc!!.longitude)
            if (!isZoomedInPosition) {
                map!!.moveCamera(CameraUpdateFactory.newLatLng(ll))
                map!!.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_FORCE_ZOOM_LEVEL))
                isZoomedInPosition = true
            }
        }
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {
        stationList = event.list
        maybeRedrawMarkers()
    }

    @Subscribe fun onShowStationOnMapRequested(event: ShowStationOnMapEvent) {
        viewPager.setCurrentItem(0, true)
        map?.moveCamera(CameraUpdateFactory.newLatLng(event.station.getLatLng()))
        val marker = markers.find { it.tag == event.station.id } as Marker
        marker.showInfoWindow()
    }

    fun maybeRedrawMarkers() {
        if (stationList != null && map != null) {
            map!!.clear()
            markers = ArrayList<Marker>()
            for (station: Station in stationList!!) {
                val marker = map!!.addMarker(station.getMarkerOptions(this))
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
        val station = stationList?.find { it.id == id } ?: return root
        stationName.text = station.nameEn
        description.text = station.descriptionEn
        bikesPresent.text = station.presentBikes.toString()
        parkingSpots.text = "P " + station.parkingSpots.toString()
        timeUpdated.text = "Updated " + station.date + " CST"
        distance.text = getFormattedDistance(station.getDistanceFrom(lastLoc))
        return root
    }

    inner class PagesAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return mapFragment
                1 -> return listFragment
            }
            return null
        }
    }
}
