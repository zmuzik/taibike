package zmuzik.ubike.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import zmuzik.ubike.MainScreenPresenter

@Module
class MainScreenModule(private val mActivity: Activity) {

    @Provides
    internal fun provideContext(): Context {
        return mActivity
    }

    @Provides
    internal fun provideActivity(): Activity {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun providePresenter(): MainScreenPresenter {
        return MainScreenPresenter()
    }
}
