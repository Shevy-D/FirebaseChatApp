package com.shevy.firebasechatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class UserAdapter(private val users: ArrayList<User>, private var listener: OnUserClickListener) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.avatarImageView.setImageResource(currentUser.avatarMockUpResource)
        holder.userNameTextView.text = currentUser.name
    }

    override fun getItemCount(): Int {
        return users.size
    }

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    class UserViewHolder(itemView: View, listener: OnUserClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var avatarImageView: ImageView
        var userNameTextView: TextView

        init {
            avatarImageView = itemView.findViewById(R.id.avatarImageView)
            userNameTextView = itemView.findViewById(R.id.userNameTextView)
            itemView.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position)
                    }
                }
            }
        }
    }
}