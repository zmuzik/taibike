package zmuzik.taibike.bus

import zmuzik.taibike.model.Station
import  android.location.Location

class StationsUpdatedEvent(val list: List<Station>, val location: Location?)
