package zmuzik.taibike

import com.google.android.gms.maps.model.LatLng

object Conf {

    val API_ROOT_TAIPEI = "http://data.taipei/youbike"

    val API_ROOT_NEW_TAIPEI = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000352-001"

    val STATIONS_MAX_UPDATE_INTERVAL = 10_000L // 10s

    val INITIAL_FORCE_ZOOM_LEVEL: Float = 16f
    val CITY_ZOOM_LEVEL: Float = 14f
    val PREF_MIN_ZOOM_LEVEL: Float = 10f
    val PREF_MAX_ZOOM_LEVEL: Float = 20f
    val TAIPEI_CENTER_COORDS = LatLng(25.0410, 121.5438)

    val MAX_DISTANCE_FROM_TAIPEI = 60 // 60km
}