package zmuzik.taibike.di

import android.content.Context
import dagger.Component
import zmuzik.taibike.MainActivity
import zmuzik.taibike.MainScreenPresenter
import zmuzik.taibike.StationsListAdapter
import zmuzik.taibike.StationsListFragment


@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MainScreenModule::class))
interface MainScreenComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(presenter: MainScreenPresenter)

    fun inject(adapter: StationsListAdapter)

    val activityContext: Context

}