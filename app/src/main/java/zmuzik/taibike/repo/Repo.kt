package zmuzik.taibike.repo

import android.arch.lifecycle.MutableLiveData
import android.util.JsonReader
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import zmuzik.taibike.Conf
import zmuzik.taibike.repo.entity.Station
import java.io.InputStreamReader

class Repo(val okHttpClient: OkHttpClient) {

    val locationLd = MutableLiveData<LatLng>()

    val stationsLd = MutableLiveData<ApiResource<List<Station>>>()
    var stationsUpdated = -1L

    fun refreshStations(forceApiCall: Boolean = false) {
        val now = System.currentTimeMillis()
        if (forceApiCall || stationsUpdated + Conf.STATIONS_MAX_UPDATE_INTERVAL < now) {
            stationsUpdated = now
            stationsLd.value = ApiResource.Loading()
            async(CommonPool) {
                try {
                    val tpStations = processApiResponseTaipei(getCall(Conf.API_ROOT_TAIPEI).execute())
                    val ntpStations = processApiResponseNewTaipei(getCall(Conf.API_ROOT_NEW_TAIPEI).execute())
                    stationsLd.postValue(ApiResource.Success(tpStations.union(ntpStations).toList()))
                } catch (e: Exception) {
                    stationsLd.postValue(ApiResource.Failure(e))
                }
            }
        }
    }

    private fun getCall(url: String) = okHttpClient.newCall(Request.Builder().url(url).build())

    private fun processApiResponseTaipei(response: Response) = processApiResponse(response, { reader ->
        val result = mutableListOf<Station>()
        while (reader.hasNext()) {
            // skip the "key/id"
            reader.nextName()
            // process the station json
            result.add(getStation(reader))
        }
        return@processApiResponse result
    })

    private fun processApiResponseNewTaipei(response: Response) = processApiResponse(response, { reader ->
        val result = mutableListOf<Station>()
        while (reader.nextName() != "records") reader.skipValue()
        reader.beginArray()
        while (reader.hasNext()) result.add(getStation(reader, 1_000_000))
        reader.endArray()
        return@processApiResponse result
    })

    // parsing code common for both apis
    private fun processApiResponse(response: Response, parseFun: (reader: JsonReader) -> List<Station>): List<Station> {
        val result = mutableListOf<Station>()
        val stream = response.body()?.byteStream() ?: return result
        val reader = JsonReader(InputStreamReader(stream, "UTF-8"))
        try {
            reader.beginObject()
            reader.nextName()
            reader.skipValue()
            reader.nextName()
            reader.beginObject()

            result.addAll(parseFun(reader))

            reader.endObject()
            reader.endObject()
        } finally {
            reader.close()
        }
        return result
    }

    private fun getStation(reader: JsonReader, idOffset: Int = 0): Station {
        val station = Station()
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            val value = reader.nextString()
            when (name) {
                "sno" -> station.id = idOffset + value.toInt()
                "sna" -> station.nameCn = value
                "tot" -> station.totalBikes = value.toInt()
                "sbi" -> station.presentBikes = value.toInt()
                "sarea" -> station.areaCn = value
                "mday" -> station.date = value.substring(8, 10) +
                        ":" + value.substring(10, 12) +
                        ":" + value.substring(12, 14)
                "lat" -> station.lat = value.toDouble()
                "lng" -> station.lng = value.toDouble()
                "ar" -> station.descriptionCn = value
                "sareaen" -> station.areaEn = value
                "snaen" -> station.nameEn = value
                "aren" -> station.descriptionEn = value
                "bemp" -> station.parkingSpots = value.toInt()
                "act" -> station.act = value.toInt()
            }
        }
        reader.endObject()
        return station
    }
}