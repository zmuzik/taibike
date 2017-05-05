package zmuzik.ubike

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(app: App)

    fun inject(activity: MainActivity)

    fun inject(presenter: Presenter)
}