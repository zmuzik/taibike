package zmuzik.taibike.screens.main

import androidx.lifecycle.ViewModel
import zmuzik.taibike.common.SingleLiveEvent
import zmuzik.taibike.repo.Repo

class MainScreenViewModel(val repo: Repo) : ViewModel() {

    val locationLd = repo.locationLd

    val stationsLd = repo.stationsLd

    val showMapEvent = SingleLiveEvent<Boolean>()

    val showStationOnMapEvent = SingleLiveEvent<Int>()

    fun refreshStations(forceApiCall : Boolean = false) = repo.refreshStations(forceApiCall)

    fun showStationOnMap(id: Int) {
        showMapEvent.postValue(true)
        showStationOnMapEvent.postValue(id)
    }
}