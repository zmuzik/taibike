package zmuzik.taibike

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        startKoin(this, listOf(appModule))

        if (LeakCanary.isInAnalyzerProcess(this)) return
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
        }
    }
}
