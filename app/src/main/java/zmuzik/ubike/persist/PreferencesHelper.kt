package zmuzik.ubike.persist

import android.content.SharedPreferences

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject
constructor(internal var mSharedPreferences: SharedPreferences)
