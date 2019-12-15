package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemInviteBinding
import com.example.socialapp.model.User

class InvitesAdapter(
    private val inviteItems: List<User>,
    private val listener: onInviteItemClickListener
) : RecyclerView.Adapter<InvitesAdapter.InviteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInviteBinding.inflate(inflater, parent, false)
        return InviteViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = inviteItems.size

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) =
        holder.bind(inviteItems[position])

    class InviteViewHolder(
        private val binding: ItemInviteBinding,
        private val listener: onInviteItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user

            binding.inviteProfilePicture.setOnClickListener { listener.openUserProfile(user.uid) }
            binding.inviteUserName.setOnClickListener { listener.openUserProfile(user.uid) }
            binding.btnInviteAccept.setOnClickListener { listener.acceptInvite(user.uid) }
            binding.btnInviteDelete.setOnClickListener { listener.deleteInvite(user.uid) }

            binding.executePendingBindings()
        }
    }

    interface onInviteItemClickListener {
        fun acceptInvite(uid: String)
        fun deleteInvite(uid: String)
        fun openUserProfile(uid: String)
    }

}