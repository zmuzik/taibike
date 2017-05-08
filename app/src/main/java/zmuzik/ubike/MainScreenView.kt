package zmuzik.ubike

import android.location.Location


interface MainScreenView {

    fun onLocationChanged(loc: Location)

}