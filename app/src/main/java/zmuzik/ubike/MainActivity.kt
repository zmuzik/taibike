package zmuzik.ubike

import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import zmuzik.ubike.di.DaggerMainScreenComponent
import zmuzik.ubike.di.MainScreenComponent
import zmuzik.ubike.di.MainScreenModule
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var mPresenter: MainScreenPresenter

    lateinit var mComponent: MainScreenComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(this)
        viewPager.adapter = PagesAdapter(supportFragmentManager)
    }

    private fun inject() {
        mComponent = DaggerMainScreenComponent.builder()
                .appComponent(App.mAppComponent)
                .mainScreenModule(MainScreenModule(this))
                .build()
        mComponent.inject(this)
        mComponent.inject(mPresenter)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> viewPager.currentItem = 0
            R.id.navigation_list -> viewPager.currentItem = 1
        }
        return true
    }

    fun onLocationChanged(loc: Location) {

    }

    private class PagesAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return SupportMapFragment.newInstance()
                1 -> return StationsListFragment()
            }
            return null
        }
    }
}
