package zmuzik.ubike;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import zmuzik.ubike.di.ActivityScope;

@ActivityScope
public class MainScreenPresenter {

    private final static String API_URL = "http://data.taipei/youbike";
    private final static int REQUEST_PERMISSION_LOC = 101;

    @Inject
    OkHttpClient mOkHttpClient;
//    @Inject
//    LocationManager mLocationManager;
    @Inject
    Context mContext;

    @Inject
    public MainScreenPresenter() {
    }

    void onResume() {
    }


    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    void requestStationsData(String url) {
        Request request = new okhttp3.Request.Builder().url(url).build();
    }

    void onPause() {

    }
}
