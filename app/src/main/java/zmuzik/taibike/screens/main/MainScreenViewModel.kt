package zmuzik.taibike.screens.main

import android.arch.lifecycle.ViewModel
import zmuzik.taibike.common.SingleLiveEvent
import zmuzik.taibike.repo.Repo

class MainScreenViewModel(repo: Repo) : ViewModel() {

    val location = repo.location

    val switchToMapEvent = SingleLiveEvent<Boolean>()

    fun switchToMap() = switchToMapEvent.postValue(true)
}