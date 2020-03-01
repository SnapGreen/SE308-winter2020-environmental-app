package com.acme.snapgreen.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class NetworkManager private constructor(private val mCtx: Context) {
    private var mRequestQueue: RequestQueue?
    // getApplicationContext() is key. It should not be activity context,
    // or else RequestQueue won’t last for the lifetime of your app
    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) { // getApplicationContext() is key. It should not be activity context,
                // or else RequestQueue won’t last for the lifetime of your app
                mRequestQueue =
                    Volley.newRequestQueue(mCtx.applicationContext)
            }
            return mRequestQueue
        }

    fun addToRequestQueue(req: Request<*>?) {
        requestQueue!!.add(req)
    }

    companion object {
        private var mInstance: NetworkManager? = null
        private var mApplicationContext : Context? = null

        /**
         * This needs to be called within an activity in order to use the network manager
         * outside of a UI class
         */
        @Synchronized
        fun getInstance(context: Context): NetworkManager? {
            if (mInstance == null) {
                mInstance = NetworkManager(context)
            }
            if (mApplicationContext == null){
                mApplicationContext = context
            }
            return mInstance
        }

        @Synchronized
        fun getInstance() : NetworkManager? {
            assert(mInstance != null && mApplicationContext != null)
            return mInstance
        }
    }

    init {
        mRequestQueue = requestQueue
    }
}