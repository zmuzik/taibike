package zmuzik.taibike.model

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import zmuzik.taibike.R
import zmuzik.taibike.utils.geoDistance
import zmuzik.taibike.utils.getBitmapDescriptor

data class Station(
        var id: Int,
        var nameCn: String,
        var totalBikes: Int,
        var presentBikes: Int,
        var areaCn: String,
        var date: String,
        var lat: Double,
        var lng: Double,
        var descriptionCn: String,
        var areaEn: String,
        var nameEn: String,
        var descriptionEn: String,
        var parkingSpots: Int,
        var act: Int) {
    constructor() : this(0, "", 0, 0, "", "", 0.0, 0.0, "", "", "", "", 0, 0)

    fun getMarkerOptions(context: Context): MarkerOptions {
        return MarkerOptions()
                .position(LatLng(lat, lng))
                .anchor(1.0f, 1.0f)
                .icon(getBitmapDescriptor(context, R.drawable.ic_pin_green))
    }

    fun getDistanceFrom(loc: Location?): Double {
        if (loc == null) {
            return -1.0
        } else {
            return geoDistance(lat, lng, loc.latitude, loc.longitude)
        }
    }

    fun getLatLng(): LatLng = LatLng(lat, lng)
}