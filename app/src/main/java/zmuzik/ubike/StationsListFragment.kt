package zmuzik.ubike

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}
