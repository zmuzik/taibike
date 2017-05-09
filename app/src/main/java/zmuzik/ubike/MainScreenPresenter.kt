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
import okhttp3.OkHttpClient
import zmuzik.ubike.bus.LocationUpdatedEvent
import zmuzik.ubike.bus.StationsUpdatedEvent
import zmuzik.ubike.bus.UiBus
import zmuzik.ubike.di.ActivityScope
import zmuzik.ubike.model.Station
import zmuzik.ubike.utils.processApiResponseNewTaipei
import zmuzik.ubike.utils.processApiResponseTaipei
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@ActivityScope
class MainScreenPresenter @Inject
constructor() : LocationListener {

    val API_URL_TAIPEI = "http://data.taipei/youbike"
    val API_URL_NEW_TAIPEI = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000352-001"

    val REQUEST_PERMISSION_LOC = 101

    @Inject
    lateinit var mActivity: Activity

    @Inject
    lateinit var mOkHttpClient: OkHttpClient

    @Inject
    lateinit var mLocationManager: LocationManager

    var stations: HashMap<Int, Station> = HashMap()

    fun onResume() {
        requestLocation()
        requestStationsData(API_URL_TAIPEI)
        requestStationsData(API_URL_NEW_TAIPEI)
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
                val stream: InputStream = response.body().byteStream()
                var stationsList: ArrayList<Station>? = null
                if (url == API_URL_TAIPEI) {
                    stationsList = processApiResponseTaipei(stream)
                } else if (url == API_URL_NEW_TAIPEI) {
                    stationsList = processApiResponseNewTaipei(stream)
                }
                for (station: Station in stationsList!!) {
                    stations.put(station.id, station)
                }
                val sortedStations: List<Station> = ArrayList(stations.values)
                UiBus.get().post(StationsUpdatedEvent(sortedStations))
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            UiBus.get().post(LocationUpdatedEvent(location))
            mLocationManager.removeUpdates(this)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}
