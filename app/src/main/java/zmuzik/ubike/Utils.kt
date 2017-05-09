package zmuzik.ubike


fun degToRad(deg: Double): Double {
    return deg * Math.PI / 180
}

fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6378

    val dLat = degToRad(lat2 - lat1)
    val dLon = degToRad(lng2 - lng1)

    val adjLat1 = degToRad(lat1)
    val adjLat2 = degToRad(lat2)

    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(adjLat1) * Math.cos(adjLat2)
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadiusKm * c;
}