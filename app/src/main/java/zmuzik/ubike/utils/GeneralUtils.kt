package zmuzik.ubike.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


fun degToRad(deg: Double): Double = deg * Math.PI / 180

fun geoDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6378

    val dLat = degToRad(lat2 - lat1)
    val dLon = degToRad(lng2 - lng1)

    val adjLat1 = degToRad(lat1)
    val adjLat2 = degToRad(lat2)

    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(adjLat1) * Math.cos(adjLat2)
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadiusKm * c
}

fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()

fun getBitmapDescriptor(context: Context, id: Int): BitmapDescriptor {
    val vectorDrawable = context.resources.getDrawable(id)
    val w = dpToPx(vectorDrawable.intrinsicWidth)
    val h = dpToPx(vectorDrawable.intrinsicHeight)
    vectorDrawable.setBounds(0, 0, w, h)
    val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}