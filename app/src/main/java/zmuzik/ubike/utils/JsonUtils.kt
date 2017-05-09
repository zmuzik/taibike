package zmuzik.ubike.utils

fun processApiResponseTaipei(stream: java.io.InputStream): ArrayList<zmuzik.ubike.model.Station> {
    val result: ArrayList<zmuzik.ubike.model.Station> = ArrayList()
    val reader: android.util.JsonReader = android.util.JsonReader(java.io.InputStreamReader(stream, "UTF-8"))
    try {
        reader.beginObject()
        reader.nextName()
        reader.skipValue()
        reader.nextName()
        reader.beginObject()
        while (reader.hasNext()) {
            // skip the "key/id"
            reader.nextName()
            // process the station json
            result.add(zmuzik.ubike.utils.getStation(reader))
        }
        reader.endObject()
        reader.endObject()
    } finally {
        reader.close()
    }
    return result
}

fun processApiResponseNewTaipei(stream: java.io.InputStream): ArrayList<zmuzik.ubike.model.Station> {
    val result: ArrayList<zmuzik.ubike.model.Station> = ArrayList()
    val reader: android.util.JsonReader = android.util.JsonReader(java.io.InputStreamReader(stream, "UTF-8"))
    try {
        reader.beginObject()
        reader.nextName()
        reader.skipValue()
        reader.nextName()
        reader.beginObject()
        while (reader.nextName() != "records") reader.skipValue()
        reader.beginArray()
        while (reader.hasNext()) {
            result.add(zmuzik.ubike.utils.getStation(reader))
        }
        reader.endArray()
        reader.endObject()
        reader.endObject()
    } finally {
        reader.close()
    }
    return result
}

fun getStation(reader: android.util.JsonReader): zmuzik.ubike.model.Station {
    val station: zmuzik.ubike.model.Station = zmuzik.ubike.model.Station()
    reader.beginObject()
    while (reader.hasNext()) {
        val name = reader.nextName()
        val value = reader.nextString()
        when (name) {
            "sno" -> station.id = value.toInt()
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
            "bemp" -> station.bemp = value.toInt()
            "act" -> station.act = value.toInt()
        }
    }
    reader.endObject()
    return station
}