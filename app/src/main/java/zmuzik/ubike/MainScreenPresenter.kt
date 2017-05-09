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
import android.util.JsonReader
import okhttp3.OkHttpClient
import zmuzik.ubike.bus.LocationUpdatedEvent
import zmuzik.ubike.bus.StationsUpdatedEvent
import zmuzik.ubike.bus.UiBus
import zmuzik.ubike.di.ActivityScope
import zmuzik.ubike.model.Station
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
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

    var mStationsList: ArrayList<Station>? = ArrayList()

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
                processStationsData(response.body().byteStream())
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    fun processStationsData(stream: InputStream) {
        mStationsList = ArrayList()
        val reader: JsonReader = JsonReader(InputStreamReader(stream, "UTF-8"))
        try {
            reader.beginObject()
            reader.nextName()
            reader.skipValue()
            reader.nextName()
            reader.beginObject()
            while (reader.hasNext()) {
                // skip the "key/id"
                reader.nextName()
                // BEGIN station
                reader.beginObject()
                val station: Station = Station()
                while (reader.hasNext()) {
                    val name = reader.nextName()
                    val value = reader.nextString()
                    when (name) {
                        "sno" -> station.stationNumber = value
                        "sna" -> station.stationName = value
                        "tot" -> station.totalBikes = value
                        "sbi" -> station.presentBikes = value
                        "sarea" -> station.area = value
                        "mday" -> station.date = value
                        "lat" -> station.lat = value
                        "lng" -> station.lng = value
                        "ar" -> station.description = value
                        "sareaen" -> station.areaEng = value
                        "snaen" -> station.stationNameEng = value
                        "aren" -> station.descriptionEng = value
                        "bemp" -> station.bemp = value
                        "act" -> station.act = value
                    }
                }
                mStationsList?.add(station)
                reader.endObject()
                // END station
            }
            reader.endObject()
            reader.endObject()
        } finally {
            reader.close()
        }

        UiBus.get().post(StationsUpdatedEvent(mStationsList))
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            UiBus.get().post(LocationUpdatedEvent(location))
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}
