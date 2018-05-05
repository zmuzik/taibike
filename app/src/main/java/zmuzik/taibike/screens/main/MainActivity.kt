package zmuzik.taibike.screens.main

import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.battswap.location.LocationLd
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import zmuzik.taibike.R
import zmuzik.taibike.common.isLocationPermissionGranted

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    val REQUEST_PERMISSION_LOCATION = 100

    val viewModel: MainScreenViewModel by viewModel()

    var locationLd: LocationLd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager.adapter = PagesAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = when (position) {
                    0 -> R.id.navigation_map
                    else -> R.id.navigation_list
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        })
        navigation.setOnNavigationItemSelectedListener(this)

        locationLd = LocationLd(this).also { it.observe(this, Observer { it?.let { viewModel.location.postValue(it) } }) }
        viewModel.switchToMapEvent.observe(this, Observer { it?.let { viewPager.currentItem = 0 } })
    }

    override fun onStart() {
        super.onStart()
        if (!isLocationPermissionGranted()) requestLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationLd?.startLocationUpdates()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> viewPager.currentItem = 0
            R.id.navigation_list -> viewPager.currentItem = 1
        }
        return true
    }

    private fun requestLocationPermission() = ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)

    inner class PagesAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int) = when (position) {
            0 -> StationsMapFragment()
            else -> StationsListFragment()
        }
    }
}