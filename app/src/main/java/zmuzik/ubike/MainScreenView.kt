package zmuzik.ubike

import android.location.Location
import zmuzik.ubike.model.Station


interface MainScreenView {

    fun onLocationChanged(loc: Location)

    fun updateStations(mStationsList: List<Station>?)
}