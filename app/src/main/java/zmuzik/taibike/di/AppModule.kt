package zmuzik.taibike.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import android.preference.PreferenceManager

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import zmuzik.taibike.App

@Module
class AppModule(internal var mApplication: Application) {

    @Provides
    internal fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    internal fun provideApp(): App {
        return mApplication as App
    }

    @Provides
    internal fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    internal fun provideLocationManager(): LocationManager {
        return mApplication.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    internal fun provideOkHttp(): OkHttpClient {
        return OkHttpClient()
    }

}
