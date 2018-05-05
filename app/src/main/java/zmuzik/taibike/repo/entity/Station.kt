package zmuzik.taibike.repo.entity

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import zmuzik.taibike.R
import zmuzik.taibike.common.geoDistance
import zmuzik.taibike.common.getBitmapDescriptor

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

    fun getMarkerOptions(context: Context) = MarkerOptions()
            .position(LatLng(lat, lng))
            .anchor(1.0f, 1.0f)
            .icon(getBitmapDescriptor(context, getIcon()))

    fun getIcon() = when {
        presentBikes > 0 && parkingSpots > 0 -> R.drawable.ic_pin_green
        presentBikes == 0 && parkingSpots > 0 -> R.drawable.ic_pin_orange
        presentBikes > 0 && parkingSpots == 0 -> R.drawable.ic_pin_red
        else -> R.drawable.ic_pin_gray
    }

    fun getDistanceFrom(loc: LatLng?) = if (loc == null) -1.0 else geoDistance(lat, lng, loc.latitude, loc.longitude)

    fun getLocation() = LatLng(lat, lng)
}