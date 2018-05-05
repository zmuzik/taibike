package zmuzik.taibike

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import zmuzik.taibike.repo.Repo
import zmuzik.taibike.screens.main.MainScreenViewModel
import zmuzik.taibike.screens.main.StationsListViewModel
import zmuzik.taibike.screens.main.StationsMapViewModel

val appModule: Module = applicationContext {

    bean {
        val client = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
        }
        client.build()
    }

    bean { Repo(get()) }

    viewModel { MainScreenViewModel(get()) }
    viewModel { StationsListViewModel(get()) }
    viewModel { StationsMapViewModel(get()) }
}