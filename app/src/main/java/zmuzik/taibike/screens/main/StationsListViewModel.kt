package zmuzik.taibike.screens.main

import android.arch.lifecycle.ViewModel
import zmuzik.taibike.repo.Repo
import zmuzik.taibike.repo.entity.Station

class StationsListViewModel(val repo: Repo) : ViewModel() {

    val location = repo.location

    fun getAllStations() = repo.getAllStations()

    fun showStationOnMap(id: Int) = repo.showStationOnMap(id)
}