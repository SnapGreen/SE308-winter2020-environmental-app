package com.acme.snapgreen.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acme.snapgreen.R

class FirebaseFriend(val name: String, val score: Int)

class FriendAdapter(private val friendsList: List<FirebaseFriend>) :

    RecyclerView.Adapter<FriendAdapter.FriendEntryHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class FriendEntryHolder(view: View, val name: TextView, val score: TextView) :
        RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendEntryHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_display, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.user_display_name)
        val scoreTextView = view.findViewById<TextView>(R.id.user_score)
        //val textView = view.findViewById<TextView>(R.id.user_display_name)
        // set the view's size, margins, paddings and layout parameters

        return FriendEntryHolder(view, nameTextView, scoreTextView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FriendEntryHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.text = friendsList[position].name
        holder.score.text = friendsList[position].score.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = friendsList.size
}