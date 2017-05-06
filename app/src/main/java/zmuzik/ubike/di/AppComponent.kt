package zmuzik.ubike.di

import dagger.Component
import zmuzik.ubike.App
import zmuzik.ubike.MainActivity
import zmuzik.ubike.Presenter
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(app: App)

    fun inject(activity: MainActivity)

    fun inject(presenter: Presenter)
}