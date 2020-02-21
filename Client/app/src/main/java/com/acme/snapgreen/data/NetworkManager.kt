import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

// A singleton to obtain the volley request queue without requiring a context
// context needs to be set manually within an activity before using
class NetworkManager private constructor(context: Context) {
    //for Volley API
    var requestQueue: RequestQueue


    init {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext())
    }


    companion object
    {
        private var instance: NetworkManager? = null

        @Synchronized
        fun setInstanceContext(context: Context?) {
            if (null == instance)
            {
                instance = NetworkManager(context!!)
            }
        }

        fun getInstance(): NetworkManager? {
            return instance
        }
    }


}