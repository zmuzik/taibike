package zmuzik.ubike

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(this)
        viewPager.adapter = PagesAdapter(supportFragmentManager)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_map -> viewPager.currentItem = 0
            R.id.navigation_list -> viewPager.currentItem = 1
        }
        return true
    }

    private class PagesAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

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
