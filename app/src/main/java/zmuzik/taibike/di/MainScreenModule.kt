package zmuzik.taibike.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import zmuzik.taibike.MainScreenPresenter

@Module
class MainScreenModule(private val mActivity: Activity) {

    @Provides
    @ActivityScope
    internal fun provideContext(): Context {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun provideActivity(): Activity {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun providePresenter(): MainScreenPresenter {
        return MainScreenPresenter()
    }
}
