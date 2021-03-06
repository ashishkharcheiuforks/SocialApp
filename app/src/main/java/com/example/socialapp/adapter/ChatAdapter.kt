package com.example.socialapp.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemTextMessageBinding
import com.example.socialapp.diffutil.MessagesDiffUtil
import com.example.socialapp.model.Message
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var messagesList: MutableList<Message> = mutableListOf()

    fun submitList(messagesUpdatedList: List<Message>) {
        val diffCallback = MessagesDiffUtil(
            messagesList,
            messagesUpdatedList
        )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messagesList.clear()
        messagesList.addAll(messagesUpdatedList)
        diffResult.dispatchUpdatesTo(this@ChatAdapter)
    }

    val authenticatedUserId = FirebaseAuth.getInstance().uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTextMessageBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = messagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }

    inner class ViewHolder(private val binding: ItemTextMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            if (item.createdByUserId != authenticatedUserId) {
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.START
                )
                binding.messageRoot.layoutParams =
                    params
            }
            binding.message = item
        }
    }
}