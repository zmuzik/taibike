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

class StationsListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_station_list, container, false)
        recyclerView = rootView as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view?.context)
        return rootView
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {
        if (event.list != null) {
            recyclerView.adapter = StationsListAdapter(event.list)
        }
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
