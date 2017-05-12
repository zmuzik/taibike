package zmuzik.ubike.bus

import zmuzik.ubike.model.Station
import  android.location.Location

class StationsUpdatedEvent(val list: List<Station>?, val location: Location?)
