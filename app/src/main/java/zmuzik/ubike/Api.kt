package zmuzik.ubike

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET

interface Api {

    @GET("blobyoubike/YouBikeTP.gz")
    fun getUbikeData(): Call<JSONObject>
}