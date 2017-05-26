package zmuzik.taibike

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import zmuzik.taibike.bus.LocationUpdatedEvent
import zmuzik.taibike.bus.ShowStationOnMapEvent
import zmuzik.taibike.bus.StationsUpdatedEvent
import zmuzik.taibike.bus.UiBus
import zmuzik.taibike.di.ActivityScope
import zmuzik.taibike.model.Station
import zmuzik.taibike.utils.processApiResponseNewTaipei
import zmuzik.taibike.utils.processApiResponseTaipei
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@ActivityScope
class MainScreenPresenter @Inject constructor() : LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    val API_URL_TAIPEI = "http://data.taipei/youbike"
    val API_URL_NEW_TAIPEI = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000352-001"

    val REQUEST_PERMISSION_LOC = 101
    val UPDATE_INTERVAL = 30000L //30 seconds
    val FASTEST_UPDATE_INTERVAL = 5000L //5 seconds
    val SMALLEST_DISPLACEMENT = 10f //10 meters

    @Inject
    lateinit var mActivity: Activity

    @Inject
    lateinit var mOkHttpClient: OkHttpClient

    var mGoogleApiClient: GoogleApiClient? = null

    var stations: HashMap<Int, Station> = HashMap()
    var location: Location? = null
    var isStarted: Boolean = false

    fun onStart() {
        if (isStarted) return
        isStarted = true
        requestLocation()
        requestStationsData(API_URL_TAIPEI, ::processApiResponseTaipei)
        requestStationsData(API_URL_NEW_TAIPEI, ::processApiResponseNewTaipei)
    }

    fun onStop() {
        if (!isStarted) return
        isStarted = false
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    fun isLocPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocation() {
        if (isLocPermission()) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = GoogleApiClient.Builder(mActivity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build()
            }
            mGoogleApiClient?.connect()
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

    fun requestStationsData(url: String, processApi: (stream: InputStream) -> ArrayList<Station>?) {
        val request = okhttp3.Request.Builder().url(url).build()
        mOkHttpClient.newCall(request).enqueue(object : okhttp3.Callback {

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val stream: InputStream = response.body().byteStream()
                val stationsList: ArrayList<Station>? = processApi(stream)
                for (station: Station in stationsList!!) {
                    stations.put(station.id, station)
                }
                maybePublishStationsUpdate()
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun maybePublishStationsUpdate() {
        if (stations.isEmpty()) return

        val sortedStations: List<Station> = if (location == null) {
            ArrayList(stations.values)
        } else {
            ArrayList(stations.values).sortedBy { it.getDistanceFrom(location!!) }
        }
        UiBus.get().post(StationsUpdatedEvent(sortedStations, location))
    }

    fun showStationOnMap(station: Station) {
        UiBus.get().post(ShowStationOnMapEvent(station))
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        if (!isLocPermission()) return
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, this)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient?.connect()
    }

    override fun onLocationChanged(loc: Location?) {
        if (loc != null) {
            location = loc
            UiBus.get().post(LocationUpdatedEvent(loc))
            maybePublishStationsUpdate()
        }
    }
}
