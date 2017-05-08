package zmuzik.ubike

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import okhttp3.OkHttpClient
import zmuzik.ubike.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class MainScreenPresenter @Inject
constructor() {

    val API_URL = "http://data.taipei/youbike"
    val REQUEST_PERMISSION_LOC = 101

    @Inject
    lateinit var mContext: Context

    @Inject
    lateinit var mOkHttpClient: OkHttpClient

    @Inject
    lateinit var mLocationManager: LocationManager

    fun onResume() {}


    fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //        Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    fun requestStationsData(url: String) {
        val request = okhttp3.Request.Builder().url(url).build()
    }

    fun onPause() {

    }
}
