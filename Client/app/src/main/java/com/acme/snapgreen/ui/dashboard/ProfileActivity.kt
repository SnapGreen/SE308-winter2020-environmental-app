package com.acme.snapgreen.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import kotlinx.android.synthetic.main.user_display.*


class ProfileActivity : AppCompatActivity() {

    lateinit var mCancel: TextView
    lateinit var mDone: TextView
    lateinit var mUserImage: ImageView

    //lateinit var mChangeImage: EditText
    lateinit var mName: EditText
    lateinit var mUserName: EditText
    lateinit var mPhoneNumber: EditText
    lateinit var mEmail: EditText
    lateinit var mPassword: EditText
//    lateinit var mDataBase: DatabaseReference
//    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        mUserImage = findViewById(R.id.profilePicImageView)
        mName = findViewById(R.id.profileFirstName)
        mUserName = findViewById(R.id.profileUserName)
        mPhoneNumber = findViewById(R.id.profilePhone)
        mEmail = findViewById(R.id.profileEmail)
        mPassword = findViewById(R.id.profilePassword)
        mDone = findViewById(R.id.profileDone)
        mCancel = findViewById(R.id.profileCancel)
//        mAuth = FirebaseAuth.getInstance()
//        val uid = mAuth.currentUser?.uid
//
//        mDataBase = FirebaseDatabase.getInstance().getReference("Users").child(uid) //.getReference("Users")?

        mDone.setOnClickListener {

            val firstName = mName.text.toString().trim()
            val lastName = mUserName.text.toString().trim()
            val phoneNum = mPhoneNumber.text.toString().trim()
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (TextUtils.isEmpty(firstName)) {
                mName.error = "Enter First Name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(lastName)) {
                mUserName.error = "Enter Last Name"
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

            updateUser(firstName, lastName, phoneNum, email, password)

        }

        mUserImage.setOnClickListener {
            selectImage(this)
        }

    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras["data"] as Bitmap
                    mUserImage.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn =
                        arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            mUserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun updateUser(
        firstName: String,
        lastName: String,
        phoneNum: String,
        email: String,
        password: String
    ) {
        val userMap = HashMap<String, Any>()

        userMap["firstName"] = firstName
        userMap["lastName"] = lastName
        userMap["phoneNum"] = phoneNum
        userMap["email"] = email
        userMap["password"] = password

        //mDataBase.updateChildren(userMap).addOnCompleteListener { task ->

        // if (task.isSuccessful) {
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        startActivity(intent)

        //   }
    }


}


