package zmuzik.ubike.di;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zmuzik.ubike.MainScreenPresenter;
import zmuzik.ubike.MainScreenView;

@Module
public class MainScreenModule {

    private final Activity mActivity;

    public MainScreenModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    MainScreenPresenter providePresenter() {
        return new MainScreenPresenter();
    }

    @Provides
    @ActivityScope
    MainScreenView provideView() {
        return (MainScreenView) mActivity;
    }
}
