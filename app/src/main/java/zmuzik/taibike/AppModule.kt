package zmuzik.taibike

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import zmuzik.taibike.repo.Repo
import zmuzik.taibike.screens.main.MainScreenViewModel

val appModule: Module = module {

    single {
        val client = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
        }
        client.build()
    }

    single { Repo(get()) }

    viewModel { MainScreenViewModel(get()) }
}