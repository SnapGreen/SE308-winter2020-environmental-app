package com.acme.snapgreen.ui.dashboard

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acme.snapgreen.Constants
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject


class InviteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var searchUserText: EditText
    private var friendsList = mutableListOf<FirebaseFriend>()

    /**
     * Populate and display recycler (list) view of all friends
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        viewManager = LinearLayoutManager(this)
        searchUserText = findViewById(R.id.invite_user_text)
        searchUserText.onSubmit {
            addFriendFromSearchBox()
            hideKeyboard(this)
        }

        getFriends()

    }

    private fun addFriendFromSearchBox() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken: String? = task.result?.token
                    // Send token to your backend via HTTPS
                    tryAddFriend(idToken)
                } else {
                    // Handle error -> task.getException();
                }
            }
    }

    private fun getFriends() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken: String? = task.result?.token
                    // Send token to your backend via HTTPS
                    getFriendsFromBackEnd(idToken)
                } else {
                    // Handle error -> task.getException();
                }
            }
    }

    private fun tryAddFriend(idToken: String?) {
        val url = "${Constants.SERVER_URL}/friends"
        var firebaseFriend: FirebaseFriend? = null

        try {
            val jsonObj = JSONObject()
            jsonObj.put("token", idToken)
            jsonObj.put("friendUsername", searchUserText.text.toString())
            searchUserText.setText("")

            val jsonRequest = JsonObjectRequest(
                Request.Method.POST,
                url, jsonObj,
                Response.Listener { response ->
                    firebaseFriend = FirebaseFriend(
                        response.getString("username"),
                        response.getInt("score")
                    )
                    addFriendToList(firebaseFriend!!)
                },
                Response.ErrorListener {
                    Toast.makeText(
                        applicationContext, "Email could not be found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
        }
    }

    private fun getFriendsFromBackEnd(idToken: String?): List<FirebaseFriend> {
        val url = "${Constants.SERVER_URL}/friends/" + idToken
        try {
            val jsonRequest = JsonArrayRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->

                    for (i in 0 until response.length()) {
                        var friend =
                            FirebaseFriend(
                                response.getJSONObject(i).getString("username"),
                                response.getJSONObject(i).getInt("score")
                            )
                        friendsList.add(friend)
                    }

                    viewAdapter = FriendAdapter(friendsList)
                    recyclerView = findViewById<RecyclerView>(R.id.friends_recycle_view).apply {
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        setHasFixedSize(true)

                        // use a linear layout manager
                        layoutManager = viewManager

                        // specify an viewAdapter (see also next example)
                        adapter = viewAdapter

                    }
                },
                Response.ErrorListener {
                }
            )
            jsonRequest.retryPolicy = DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
        }
        return friendsList
    }


    private fun addFriendToList(friend: FirebaseFriend) {
        friendsList.add(friend)
        viewAdapter.notifyDataSetChanged()
        Toast.makeText(
            applicationContext, "Added " + friendsList.last().name,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Extends EditText to allow easy configuration of the function called when you hit "enter"
     * on the soft keyboard
     */
    private fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                func()
            }
            true
        }
    }

    /**
     * Hides the soft keyboard
     */
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}




