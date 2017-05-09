package zmuzik.ubike.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import zmuzik.ubike.utils.distance

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
        var bemp: Int,
        var act: Int) {
    constructor() : this(0, "", 0, 0, "", "", 0.0, 0.0, "", "", "", "", 0, 0)

    fun getMarkerOptions(): MarkerOptions {
        return MarkerOptions()
                .position(LatLng(lat, lng))
                .anchor(1.0f, 1.0f)
                .title(nameEn)
                .snippet(areaEn)
    }

    fun getDistanceFrom(loc: Location): Double = distance(lat, lng, loc.latitude, loc.longitude)
}