package zmuzik.taibike.screens.main

import android.arch.lifecycle.ViewModel
import zmuzik.taibike.common.SingleLiveEvent
import zmuzik.taibike.repo.Repo

class MainScreenViewModel(val repo: Repo) : ViewModel() {

    val location = repo.location

    val showMapEvent = SingleLiveEvent<Boolean>()

    val showStationOnMapEvent = SingleLiveEvent<Int>()

    fun getAllStations() = repo.getAllStations()

    fun showStationOnMap(id: Int) {
        showMapEvent.postValue(true)
        showStationOnMapEvent.postValue(id)
    }
}