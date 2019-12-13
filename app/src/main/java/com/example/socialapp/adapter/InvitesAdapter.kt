package com.example.socialapp.screens.main.invites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.InviteItemBinding
import com.example.socialapp.model.User

class InvitesAdapter(
    private val inviteItems: List<User>,
    private val viewModel: InvitesViewModel
) : RecyclerView.Adapter<InvitesAdapter.InviteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InviteItemBinding.inflate(inflater, parent, false)
        return InviteViewHolder(binding, viewModel)
    }

    override fun getItemCount(): Int = inviteItems.size

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) =
        holder.bind(inviteItems[position])

    class InviteViewHolder(
        private val binding: InviteItemBinding,
        private val viewModel: InvitesViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.viewmodel = viewModel
            binding.executePendingBindings()
        }
    }

}