package com.acme.snapgreen.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.data.Result
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.io.IOException

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        var firstName = findViewById<EditText>(R.id.create_user_first_name)
        var lastName = findViewById<EditText>(R.id.create_user_last_name)
        var email = findViewById<EditText>(R.id.create_user_email)
        var password = findViewById<EditText>(R.id.create_user_password)
        var saveUser = findViewById<Button>(R.id.save_user_btn)

        saveUser.setOnClickListener{
            val url = "http://10.0.2.2:8080/users"
            val user = JSONObject()
            user.put("firstName", firstName.text)
            user.put("lastName", lastName.text)
            user.put("username", email.text)
            user.put("password", password.text)

            try {
                val createUserRequest = JsonObjectRequest(
                    Request.Method.POST, url, user,
                    Response.Listener { response ->

                        //TODO: Handle successful link to server backend
                        //     incorrect password -> Result.Failure
                        //     Successful Authentication -> Result.success
                    },
                    Response.ErrorListener { error ->
                        error.printStackTrace()
                    }
                )
                NetworkManager.getInstance()?.addToRequestQueue(createUserRequest)
                Toast.makeText(applicationContext, "New user created!", Toast.LENGTH_SHORT).show()
                finish()
            }
            catch (e: Throwable) {
                Log.e("Create User Activity","Create user request failed")
            }
        }
    }
}