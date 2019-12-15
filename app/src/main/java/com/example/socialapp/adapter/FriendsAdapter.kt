package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemFriendBinding
import com.example.socialapp.model.User

class FriendsAdapter(private val listener: ViewHolder.OnFriendItemClickListener) :
    PagedListAdapter<User, FriendsAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFriendBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        if (user != null) {
            holder.bind(user, listener)
        }
    }


    class ViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, listener: OnFriendItemClickListener) {
            binding.user = user

            binding.constraintLayout.setOnClickListener {
                listener.onFriendItemClick(user.uid)
            }

            binding.executePendingBindings()
        }

        interface OnFriendItemClickListener {
            fun onFriendItemClick(uid: String)
        }
    }
}