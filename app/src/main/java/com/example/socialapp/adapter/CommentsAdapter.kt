package com.example.socialapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemCommentBinding
import com.example.socialapp.model.Comment
import com.example.socialapp.model.User
import com.google.firebase.firestore.FirebaseFirestore

class CommentsAdapter(
    private val comments: List<Comment>,
    private val listener: onCommentClickListener
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(comments[position])

    class CommentViewHolder(
        private val binding: ItemCommentBinding,
        private val listener: onCommentClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment

            FirebaseFirestore.getInstance()
                .document("users/${comment.createdByUserId}")
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    binding.user = user
                }

            binding.userProfilePicture.setOnClickListener { listener.openUserProfile(comment.createdByUserId) }

            binding.executePendingBindings()
        }
    }

    interface onCommentClickListener {
        fun openUserProfile(uid: String)
    }

}