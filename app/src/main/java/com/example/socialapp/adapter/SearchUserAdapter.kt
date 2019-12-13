package com.example.socialapp.screens.searchuser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.SearchUserItemBinding
import com.example.socialapp.model.User


class SearchUserAdapter(
    private val listItems: List<User>,
    private val listItemListener: SearchUserItemListener
) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchUserItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listItems[position], listItemListener)

    class ViewHolder(
        private val binding: SearchUserItemBinding
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
}