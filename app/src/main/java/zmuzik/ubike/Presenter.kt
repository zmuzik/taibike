package zmuzik.ubike

import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Presenter {

    @Inject
    lateinit var mApi : Api

    init {
        App.graph.inject(this)
    }

    fun onResume() {
        mApi.getUbikeData().enqueue(object : Callback<JSONObject> {
            override fun onResponse(call: Call<JSONObject>?, response: Response<JSONObject>?) {
                Log.d("RESP:", response?.toString())
            }

            override fun onFailure(call: Call<JSONObject>?, t: Throwable?) {
                print(t)
            }
        })
    }

    fun onPause() {

    }
}