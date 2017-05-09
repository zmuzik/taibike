package zmuzik.ubike.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
                .anchor(0.5f, 0.5f)
                .title(nameEn)
                .snippet(areaEn)
    }
}