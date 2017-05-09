package zmuzik.ubike

import android.app.Application
import zmuzik.ubike.di.AppComponent
import zmuzik.ubike.di.AppModule
import zmuzik.ubike.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}