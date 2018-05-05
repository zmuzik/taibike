package zmuzik.taibike.screens.main

import android.arch.lifecycle.ViewModel
import zmuzik.taibike.common.SingleLiveEvent
import zmuzik.taibike.repo.Repo

class StationsMapViewModel(val repo: Repo) : ViewModel() {

    val location = repo.location

    val showStationEvent = SingleLiveEvent<Int>()

    fun getAllStations() = repo.getAllStations()

    fun showStationOnMap(stationId: Int)  = showStationEvent.postValue(stationId)
}