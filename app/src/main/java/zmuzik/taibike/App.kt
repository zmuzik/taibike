package zmuzik.taibike

import android.app.Application
import zmuzik.taibike.di.AppComponent
import zmuzik.taibike.di.AppModule
import zmuzik.taibike.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}