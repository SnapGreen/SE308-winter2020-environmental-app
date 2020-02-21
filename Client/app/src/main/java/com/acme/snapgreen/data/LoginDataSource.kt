package com.acme.snapgreen.data

import NetworkManager
import com.acme.snapgreen.data.model.LoggedInUser
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.io.IOException


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {

        val queue = NetworkManager.getInstance()?.requestQueue

        try {
            val url = "http://10.0.2.2/login:8080"
            val jsonObj = JSONObject()
            jsonObj.put("name","kgoo")
            jsonObj.put("password","123")
            var fakeUser = LoggedInUser("Fail", "Fail")

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObj,
                Response.Listener { response ->
                    fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), response.toString())

                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
            )

            val socketTimeout = 3000 //30 seconds - change to what you want

            val policy: RetryPolicy = DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            jsonObjectRequest.setRetryPolicy(policy)
            queue?.add(jsonObjectRequest)
           return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

