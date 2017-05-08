package zmuzik.ubike.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import zmuzik.ubike.App;
import zmuzik.ubike.PreferencesHelper;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(PreferencesHelper helper);

    @AppContext
    Context getApplicationContext();

    Application getApplication();

    App getApp();

    SharedPreferences getSharedPreferences();

    PreferencesHelper getPrefsHelper();

    OkHttpClient getOkHttpClient();

    LocationManager getLocationManager();

}