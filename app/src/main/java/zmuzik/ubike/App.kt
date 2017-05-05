package zmuzik.ubike

import android.app.Application
import android.location.LocationManager
import javax.inject.Inject

class App : Application() {

    companion object {
        @JvmStatic lateinit var graph: AppComponent
    }

    @Inject
    lateinit var locationManager: LocationManager

    override fun onCreate() {
        super.onCreate()
        graph = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        graph.inject(this)
    }
}