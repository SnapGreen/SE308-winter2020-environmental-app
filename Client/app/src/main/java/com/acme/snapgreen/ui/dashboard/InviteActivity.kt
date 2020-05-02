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
import com.acme.snapgreen.R


class InviteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var searchUserText: EditText
    private lateinit var friendsList: MutableList<String>

    /**
     * Populate and display recycler (list) view of all friends
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        friendsList = mutableListOf(
            "ChellBellGoDingDong",
            "Bruno Da Savior",
            "HiImJai",
            "ZachinAwayOnThisProject",
            "KalenWhateverYouSay",
            "John",
            "DestinTheBestInTheWestInn",
            "Daddy Jeff",
            "TreeHugger69",
            "SmokeLoveNotTrees"
        )
        viewManager = LinearLayoutManager(this)
        viewAdapter = FriendAdapter(friendsList)
        searchUserText = findViewById(R.id.invite_user_text)
        searchUserText.onSubmit { addFriend() }
        recyclerView = findViewById<RecyclerView>(R.id.friends_recycle_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(false)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    /**
     *  Adds a friend to the friends list based on the users search query
     *  TODO: make this connect to backend
     */
    private fun addFriend() {
        friendsList.add(searchUserText.text.toString())
        viewAdapter.notifyDataSetChanged()
        Toast.makeText(
            applicationContext, "Added " + friendsList.last(),
            Toast.LENGTH_SHORT
        ).show()
        searchUserText.setText("")
        hideKeyboard(this)


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




