package zmuzik.ubike

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import zmuzik.ubike.bus.LocationUpdatedEvent
import zmuzik.ubike.bus.StationsUpdatedEvent
import zmuzik.ubike.bus.UiBus
import zmuzik.ubike.di.ActivityScope
import zmuzik.ubike.di.DaggerMainScreenComponent
import zmuzik.ubike.di.MainScreenComponent
import zmuzik.ubike.di.MainScreenModule
import zmuzik.ubike.model.Station
import javax.inject.Inject


@ActivityScope
class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    @Inject
    lateinit var mPresenter: MainScreenPresenter

    lateinit var mComponent: MainScreenComponent

    val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
    val listFragment: Fragment = StationsListFragment()

    val INITIAL_FORCE_ZOOM_LEVEL: Float = 16f
    val PREF_MIN_ZOOM_LEVEL: Float = 10f
    val PREF_MAX_ZOOM_LEVEL: Float = 20f

    var mStationList: List<Station>? = null
    var mMap: GoogleMap? = null
    var mLastLoc: Location? = null
    var mIsZoomedInPosition: Boolean = false

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
        mComponent = DaggerMainScreenComponent.builder()
                .appComponent((applicationContext as App).appComponent)
                .mainScreenModule(MainScreenModule(this))
                .build()
        mComponent.inject(this)
        mComponent.inject(mPresenter)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
        UiBus.get().register(this)
    }

    override fun onPause() {
        super.onStop()
        mPresenter.onPause()
        UiBus.get().unregister(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> viewPager.currentItem = 0
            R.id.navigation_list -> viewPager.currentItem = 1
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == mPresenter.REQUEST_PERMISSION_LOC) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.requestLocation()
                mMap?.isMyLocationEnabled = true
            }
        }
    }

    @Subscribe fun onLocationChanged(event: LocationUpdatedEvent) {
        mLastLoc = event.location
        maybeUpdateLocation()
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        mMap?.setMinZoomPreference(PREF_MIN_ZOOM_LEVEL)
        mMap?.setMaxZoomPreference(PREF_MAX_ZOOM_LEVEL)
        mMap?.uiSettings?.isZoomControlsEnabled = true
        if (mPresenter.isLocPermission()) mMap?.isMyLocationEnabled = true
        maybeUpdateLocation()
        maybeRedrawMarkers()
    }

    private fun maybeUpdateLocation() {
        if (mMap != null && mLastLoc != null) {
            val ll: LatLng = LatLng(mLastLoc!!.latitude, mLastLoc!!.longitude)
            if (!mIsZoomedInPosition) {
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(ll))
                mMap!!.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_FORCE_ZOOM_LEVEL))
                mIsZoomedInPosition = true
            }
        }
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {
        mStationList = event.list
        maybeRedrawMarkers()
    }

    fun maybeRedrawMarkers() {
        if (mStationList != null && mMap != null) {
            mMap!!.clear()
            for (station: Station in mStationList!!) {
                mMap!!.addMarker(station.getMarkerOptions())
            }
        }
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
