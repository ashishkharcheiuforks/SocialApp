package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemRecentChatRoomBinding
import com.example.socialapp.model.LastMessage

class ChatRoomsAdapter(private val listener: OnChatItemClickListener) :
    PagedListAdapter<LastMessage, ChatRoomsAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<LastMessage>() {
            override fun areItemsTheSame(oldItem: LastMessage, newItem: LastMessage): Boolean =
                oldItem.message.messageId == newItem.message.messageId

            override fun areContentsTheSame(oldItem: LastMessage, newItem: LastMessage): Boolean =
                oldItem == newItem
        }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecentChatRoomBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item, listener)
    }

    class ViewHolder(private val binding: ItemRecentChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lastMessage: LastMessage, listener: OnChatItemClickListener) {
            binding.user = lastMessage.user
            binding.message = lastMessage.message

            binding.constraintLayout.setOnClickListener {
                listener.onChatItemClick(lastMessage.user.uid)
            }

            binding.executePendingBindings()
        }

    }

    interface OnChatItemClickListener {
        fun onChatItemClick(userId: String)
    }

}