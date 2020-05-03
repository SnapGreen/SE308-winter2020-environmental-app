package com.acme.snapgreen.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

<<<<<<< HEAD
        var firstName = findViewById<EditText>(R.id.create_user_first_name)
        var lastName = findViewById<EditText>(R.id.create_user_last_name)
        var email = findViewById<EditText>(R.id.create_user_email)
        var password = findViewById<EditText>(R.id.create_user_password)
        var saveUser = findViewById<Button>(R.id.save_user_btn)
=======
        val firstName = findViewById<EditText>(R.id.create_user_first_name)
        val lastName = findViewById<EditText>(R.id.create_user_last_name)
        val email = findViewById<EditText>(R.id.create_user_email)
        val password = findViewById<EditText>(R.id.create_user_password)
        val saveUser = findViewById<Button>(R.id.save_user_btn)
>>>>>>> bb52b99e2fc093b53c82de927d1087f108467f20

        saveUser.setOnClickListener {
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
<<<<<<< HEAD
            }
            catch (e: Throwable) {
                Log.e("Create User Activity","Create user request failed")
=======
            } catch (e: Throwable) {
                Log.e("Create User Activity", "Create user request failed")
>>>>>>> bb52b99e2fc093b53c82de927d1087f108467f20
            }
        }
    }
}