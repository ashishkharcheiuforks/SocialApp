package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemCommentBinding
import com.example.socialapp.model.Comment

class CommentsAdapter(private val listener: OnCommentClickListener) :
    PagedListAdapter<Comment, CommentsAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem == newItem
        }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)

        if (comment != null) {
            holder.bind(comment, listener)
        }
    }


    class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment, listener: OnCommentClickListener) {
            // set data binding variables
            binding.comment = comment

            binding.userProfilePicture.setOnClickListener {
                listener.onProfilePictureClicked(comment.user.uid)
            }

            binding.executePendingBindings()
        }
    }


    interface OnCommentClickListener {
        fun onProfilePictureClicked(userId: String)
    }
}