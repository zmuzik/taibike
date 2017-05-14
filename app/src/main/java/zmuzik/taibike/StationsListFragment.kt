package zmuzik.taibike

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.otto.Subscribe
import zmuzik.taibike.bus.LocationUpdatedEvent
import zmuzik.taibike.bus.StationsUpdatedEvent
import zmuzik.taibike.bus.UiBus
import javax.inject.Inject


class StationsListFragment @Inject constructor() : Fragment() {

    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var presenter: MainScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_station_list, container, false)
        recyclerView = rootView as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view?.context)
        val dividerDecoration = DividerItemDecoration(recyclerView.context, LinearLayout.VERTICAL)
        recyclerView.addItemDecoration(dividerDecoration)
        return rootView
    }

    @Subscribe fun onStationListUpdated(event: StationsUpdatedEvent) {
        recyclerView.adapter = StationsListAdapter(event.list, event.location, presenter)
    }

    @Subscribe fun onLocationUpdate(event: LocationUpdatedEvent) {
        (recyclerView.adapter as StationsListAdapter).updateLocation(event.location)
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
