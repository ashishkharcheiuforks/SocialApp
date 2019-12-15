package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemAdvertBinding
import com.example.socialapp.model.Advertisement

class AdvertsAdapter(private val listener: OnAdvertisementClickListener) :
    PagedListAdapter<Advertisement, AdvertsAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Advertisement>() {
            override fun areItemsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean =
                oldItem.advertisementId == newItem.advertisementId

            override fun areContentsTheSame(
                oldItem: Advertisement,
                newItem: Advertisement
            ): Boolean =
                oldItem == newItem
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAdvertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val advert = getItem(position)

        if (advert != null) {
            holder.bind(advert, listener)
        }
    }

    class ViewHolder(private val binding: ItemAdvertBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(advertisement: Advertisement, listener: OnAdvertisementClickListener) {
            binding.advert = advertisement
            binding.filters = advertisement.filters
            binding.user = advertisement.user

            binding.btnRespond.setOnClickListener {
                listener.onRespond(advertisement.createdByUserUid!!)
            }

            binding.ivProfilePicture.setOnClickListener {
                listener.onProfilePictureClicked(advertisement.createdByUserUid!!)
            }

            binding.executePendingBindings()
        }


}
    interface OnAdvertisementClickListener {
        fun onRespond(userId: String)
        fun onProfilePictureClicked(userId: String)
    }
}

