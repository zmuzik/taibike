package zmuzik.ubike

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import okhttp3.OkHttpClient
import zmuzik.ubike.di.ActivityScope
import java.io.IOException
import javax.inject.Inject


@ActivityScope
class MainScreenPresenter @Inject
constructor() : LocationListener {

    val API_URL = "http://data.taipei/youbike"
    val REQUEST_PERMISSION_LOC = 101

    @Inject
    lateinit var mActivity: Activity

    @Inject
    lateinit var mOkHttpClient: OkHttpClient

    @Inject
    lateinit var mLocationManager: LocationManager

    @Inject
    lateinit var mView: MainScreenView


    fun onResume() {
        requestLocation()
        requestStationsData(API_URL)
    }

    fun onPause() {
        mLocationManager.removeUpdates(this)
    }

    fun isLocPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocation() {
        if (isLocPermission()) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOC)
        }
    }

    fun requestStationsData(url: String) {
        val request = okhttp3.Request.Builder().url(url).build()
        mOkHttpClient.newCall(request).enqueue(object : okhttp3.Callback {

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val result = response.body().string()
                Log.d("RESP:", result)
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            mView.onLocationChanged(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}
