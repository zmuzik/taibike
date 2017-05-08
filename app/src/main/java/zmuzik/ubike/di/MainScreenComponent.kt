package zmuzik.ubike.di

import android.content.Context
import dagger.Component
import zmuzik.ubike.MainActivity
import zmuzik.ubike.MainScreenPresenter


@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MainScreenModule::class))
interface MainScreenComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(presenter: MainScreenPresenter)

    val activityContext: Context

}