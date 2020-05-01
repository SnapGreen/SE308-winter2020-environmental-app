package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var mUserImage: ImageView
    lateinit var mFirstName: EditText
    lateinit var mLastName: EditText
    lateinit var mPhoneNumber: EditText
    lateinit var mEmail: EditText
    lateinit var mPassword: EditText
    lateinit var mUpdateButton: Button
//    lateinit var mDataBase: DatabaseReference
//    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Activity launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_profile)

        mUserImage = findViewById(R.id.profilePicImageView)
        mFirstName = findViewById(R.id.profileFirstName)
        mLastName = findViewById(R.id.profileLastName)
        mPhoneNumber = findViewById(R.id.profilePhone)
        mEmail = findViewById(R.id.profileEmail)
        mPassword = findViewById(R.id.profilePassword)
        mUpdateButton = findViewById(R.id.profileUpdateButton)

//        mAuth = FirebaseAuth.getInstance()
//        val uid = mAuth.currentUser?.uid
//
//        mDataBase = FirebaseDatabase.getInstance().getReference("Users").child(uid) //.getReference("Users")?

        mUpdateButton.setOnClickListener {

            val firstName = mFirstName.text.toString().trim()
            val lastName = mLastName.text.toString().trim()
            val phoneNum = mPhoneNumber.text.toString().trim()
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (TextUtils.isEmpty(firstName)) {
                mFirstName.error = "Enter First Name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(lastName)) {
                mLastName.error = "Enter Last Name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(phoneNum)) {
                mPhoneNumber.error = "Enter Phone Number"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                mEmail.error = "Enter Email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                mPassword.error = "Enter Password"
                return@setOnClickListener
            }

//            updateUser(firstName, lastName, phoneNum, email, password)
        }

    }

//    private fun updateUser(
//        firstName: String,
//        lastName: String,
//        phoneNum: String,
//        email: String,
//        password: String
//    ) {
//        val userMap = HashMap<String, Any>()
//
//        userMap["firstName"] = firstName
//        userMap["lastName"] = lastName
//        userMap["phoneNum"] = phoneNum
//        userMap["email"] = email
//        userMap["password"] = password
//
//        mDataBase.updateChildren(userMap).addOnCompleteListener { task ->
//
//            if (task.isSuccessful) {
//                val intent = Intent(applicationContext, DashboardActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//    }


}