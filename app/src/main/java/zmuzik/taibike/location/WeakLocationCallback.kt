package com.battswap.location


import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import java.lang.ref.WeakReference

class WeakLocationCallback(locationCallback: LocationCallback) : LocationCallback() {

    private val callbackWr: WeakReference<LocationCallback> = WeakReference(locationCallback)

    override fun onLocationResult(result: LocationResult?) {
        callbackWr.get()?.onLocationResult(result)
    }
}
