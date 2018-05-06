package zmuzik.taibike.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

fun Context.isLocationPermissionGranted() = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun View.hide(): Unit = this.setVisibility(View.GONE)

fun View.show(): Unit = this.setVisibility(View.VISIBLE)

fun Activity.toast(msg: String) = Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()

fun Activity.toast(resId: Int) = this.toast(this.applicationContext.getString(resId))

fun ProgressBar.setColor(colorId: Int) = this.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.MULTIPLY)