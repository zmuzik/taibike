package zmuzik.taibike.di

import android.app.Application
import android.content.SharedPreferences
import android.location.LocationManager

import javax.inject.Singleton

import dagger.Component
import okhttp3.OkHttpClient
import zmuzik.taibike.App
import zmuzik.taibike.persist.PreferencesHelper

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(helper: PreferencesHelper)

    val application: Application

    val app: App

    val sharedPreferences: SharedPreferences

    val prefsHelper: PreferencesHelper

    val okHttpClient: OkHttpClient

    val locationManager: LocationManager

}