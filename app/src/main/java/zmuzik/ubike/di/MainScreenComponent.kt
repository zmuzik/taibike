package zmuzik.ubike.di

import dagger.Component
import zmuzik.ubike.MainActivity
import zmuzik.ubike.MainScreenPresenter


@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MainScreenModule::class))
interface MainScreenComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(presenter: MainScreenPresenter)

}