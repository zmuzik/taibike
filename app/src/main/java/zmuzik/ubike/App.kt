package zmuzik.ubike

import android.app.Application
import zmuzik.ubike.di.AppComponent
import zmuzik.ubike.di.AppModule
import zmuzik.ubike.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var mAppComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        mAppComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    fun getComponent(): AppComponent {
        return mAppComponent
    }
}