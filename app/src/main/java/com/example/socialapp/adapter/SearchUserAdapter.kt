package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemSearchUserBinding
import com.example.socialapp.model.User


class SearchUserAdapter(
    private val listItems: List<User>,
    private val listItemListener: SearchUserItemListener
) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listItems[position], listItemListener)

    class ViewHolder(
        private val binding: ItemSearchUserBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem: User, listItemListener: SearchUserItemListener) {
            binding.user = listItem
            binding.constraintLayout.setOnClickListener {
                listItemListener.onSearchUserItemClick(listItem.uid)
            }
            binding.executePendingBindings()
        }
    }

    interface SearchUserItemListener {
        fun onSearchUserItemClick(userUid: String)
    }
}