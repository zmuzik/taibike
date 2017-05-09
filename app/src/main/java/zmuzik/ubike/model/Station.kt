package zmuzik.ubike.model

data class Station(
        var stationNumber: String,
        var stationName: String,
        var totalBikes: String,
        var presentBikes: String,
        var area: String,
        var date: String,
        var lat: String,
        var lng: String,
        var description: String,
        var areaEng: String,
        var stationNameEng: String,
        var descriptionEng: String,
        var bemp: String,
        var act: String) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "")
}