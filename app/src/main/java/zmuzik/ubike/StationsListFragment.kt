package zmuzik.ubike

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.otto.Subscribe
import zmuzik.ubike.bus.StationsUpdatedEvent
import zmuzik.ubike.bus.UiBus
import zmuzik.ubike.dummy.DummyContent

class StationsListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_station_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            val recyclerView = view
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = StationsListAdapter(DummyContent.ITEMS)
        }
        return view
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {

    }

    override fun onResume() {
        super.onResume()
        UiBus.get().register(this)
    }

    override fun onPause() {
        super.onPause()
        UiBus.get().unregister(this)
    }
}
