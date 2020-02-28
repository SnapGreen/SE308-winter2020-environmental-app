package com.acme.snapgreen.data
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
 * Build and send a POST request to the background with the username and password provided in the
 * UI using the NetworkManager singleton to access the request queue.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {

        val queue = NetworkManager.getInstance()?.requestQueue

        //TODO: connect to real server instead of localhost
        val url = "http://10.0.2.2:8080/login"
        val jsonObj = JSONObject()
        jsonObj.put("name", username)
        jsonObj.put("password", password)
        val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)

        try {

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObj,
                Response.Listener { response ->

                    //TODO: Handle successful link to server backend
                    //     incorrect password -> Result.Failure
                    //     Successful Authentication -> Result.success
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
            )

            // set length until login failure
            val socketTimeout = 3000 // 3 seconds
            val policy: RetryPolicy = DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            jsonObjectRequest.retryPolicy = policy

            queue?.add(jsonObjectRequest)

            //TODO: return result of server auth instead of fake success
            return Result.Success(user)
        }
        catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

