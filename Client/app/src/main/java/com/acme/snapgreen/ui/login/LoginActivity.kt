package com.acme.snapgreen.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.ui.dashboard.DashboardActivity
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        // set context for Valley calls for the remainder of the app
        NetworkManager.getInstance(applicationContext)
        Realm.init(applicationContext)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener { createSignInIntent() }
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, DashboardActivity::class.java).apply {
//                    putExtra(EXTRA_MESSAGE, displayName)
                }
                startActivity(intent)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response != null) {
                    Toast.makeText(
                        applicationContext,
                        "${response.error!!.errorCode}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    // [END auth_fui_result]

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
