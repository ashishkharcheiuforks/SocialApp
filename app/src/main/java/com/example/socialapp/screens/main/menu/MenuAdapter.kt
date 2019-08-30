package com.example.socialapp.screens.main.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val menuItemListener: MenuItemListener
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MenuItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(menuItems[position], menuItemListener)

    class ViewHolder(
        private val binding: MenuItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: MenuItem, menuItemListener: MenuItemListener) {
            binding.menuItem = menuItem
            binding.constraintLayout.setOnClickListener { menuItemListener.onMenuItemClick(menuItem) }
            binding.executePendingBindings()
        }
    }
}

