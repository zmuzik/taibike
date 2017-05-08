package zmuzik.ubike.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import zmuzik.ubike.App;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @AppContext
    Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    App provideApp() {
        return (App) mApplication;
    }

    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    LocationManager provideLocationManager() {
        return (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    OkHttpClient provideOkHttp() {
        return new OkHttpClient();
    }

}
