package com.battswap.location

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.LiveData
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import zmuzik.taibike.common.isLocationPermissionGranted
import java.lang.ref.WeakReference

class LocationLd(private val activity: Activity) : LiveData<LatLng>() {

    val UPDATE_INTERVAL = 60 * 1000L
    val FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2

    private var weakLocationCallbackWr: WeakReference<WeakLocationCallback>? = null

    private var locationRequest: LocationRequest = LocationRequest.create().also {
        it.interval = UPDATE_INTERVAL
        it.fastestInterval = FASTEST_UPDATE_INTERVAL
        it.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private var client: FusedLocationProviderClient? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { publishNewLocation(it.lastLocation) }
        }
    }

    override fun onActive() {
        startLocationUpdates()
    }

    override fun onInactive() {
        stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!activity.isLocationPermissionGranted()) return
        if (client == null) client = LocationServices.getFusedLocationProviderClient(activity)
        weakLocationCallbackWr = WeakReference(WeakLocationCallback(locationCallback))
        client?.requestLocationUpdates(locationRequest, weakLocationCallbackWr?.get(), Looper.myLooper())
    }

    fun stopLocationUpdates() = weakLocationCallbackWr?.get()?.let { client?.removeLocationUpdates(it) }

    fun publishNewLocation(location: Location) {
        val newLoc = LatLng(location.latitude, location.longitude)
        postValue(newLoc)
        Loc.lastKnown = newLoc
    }
}