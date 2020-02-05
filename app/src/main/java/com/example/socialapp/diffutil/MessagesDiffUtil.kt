package com.example.socialapp.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.socialapp.model.Message

class MessagesDiffUtil(
    private val oldMessages: List<Message>,
    private val newMessages: List<Message>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessages[oldItemPosition].messageId == newMessages[newItemPosition].messageId
    }

    override fun getOldListSize(): Int = oldMessages.size

    override fun getNewListSize(): Int = newMessages.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessages[oldItemPosition].createdByUserId == newMessages[newItemPosition].createdByUserId
                && oldMessages[oldItemPosition].text == newMessages[newItemPosition].text
                && oldMessages[oldItemPosition].dateCreated == newMessages[newItemPosition].dateCreated
    }
}