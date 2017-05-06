package zmuzik.ubike

import android.location.Location
import android.location.LocationManager
import android.util.Log
import okhttp3.OkHttpClient
import java.io.IOException
import javax.inject.Inject

class Presenter {

    val API_URL = "http://data.taipei/youbike"

    @Inject
    lateinit var mOkHttpClient: OkHttpClient

    @Inject
    lateinit var mLocationManager : LocationManager

    init {
        App.graph.inject(this)
    }

    fun onResume() {
        requestStationsData(API_URL)
        requestLocation()
    }

    private fun requestLocation() {
        val loc : Location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    private fun requestStationsData(url: String) {
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

    fun onPause() {

    }
}