package com.example.socialapp.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.databinding.ItemPostBinding
import com.example.socialapp.model.Post
import com.example.socialapp.module.GlideApp
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class PostsAdapter(private val listener: OnPostClickListener) :
    PagedListAdapter<Post, PostsAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }) {

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // Clear the subscription when the view gets off the screen
        holder.viewHolderDisposables.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)

        if (post != null) {
            holder.bind(post, listener)
        }
    }


    class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        val viewHolderDisposables = CompositeDisposable()

        fun bind(post: Post, listener: OnPostClickListener) {
            // set data binding variables
            binding.post = post
            binding.user = post.user

            if(post.postImage != null){
                binding.postItemPostImage.visibility = View.VISIBLE
                GlideApp.with(binding.root.context).load(post.postImage).into(binding.postItemPostImage)
            }

            // subscribe Observable that holds realtime like status of currently displayed post
            post.postLiked.subscribeBy(onNext = { isPostLiked ->

                // Set suitable image drawable and onClickListener for like button
                binding.buttonPostItemLike.apply {
                    if (isPostLiked) {
                        setImageResource(R.drawable.ic_favorite_color_primary_24dp)
                        setOnClickListener { listener.onUnlikeButtonClicked(post.postId) }
                    } else {
                        setImageResource(R.drawable.ic_favorite_border_color_primary_24dp)
                        setOnClickListener { listener.onLikeButtonClicked(post.postId) }
                    }
                }


            },
                onError = {

                }).addTo(viewHolderDisposables)


            // set On Click Listeners
            binding.buttonPostItemLike.setOnClickListener {
                listener.onLikeButtonClicked(post.postId)
            }

            binding.buttonPostItemComment.setOnClickListener {
                listener.onCommentButtonClicked(post.postId)
            }

            binding.imagePostItemUserProfilePicture.setOnClickListener {
                listener.onProfilePictureClicked(post.user!!.uid)
            }

            binding.executePendingBindings()
        }
    }


    interface OnPostClickListener {
        fun onLikeButtonClicked(postId: String)
        fun onUnlikeButtonClicked(postId: String)
        fun onCommentButtonClicked(postId: String)
        fun onProfilePictureClicked(userId: String)
    }
}
