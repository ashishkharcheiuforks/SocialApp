package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.databinding.ItemAdvertBinding
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore

class AdsAdapter(
    private val listener: OnAdvertisementClickListener,
    options: FirestorePagingOptions<Advertisement>
) :
    FirestorePagingAdapter<Advertisement, AdsAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAdvertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: Advertisement
    ) = holder.bind(model, listener)

    class ViewHolder(private val binding: ItemAdvertBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val db = FirebaseFirestore.getInstance()

        fun bind(advertisement: Advertisement, listener: OnAdvertisementClickListener) {
            binding.advert = advertisement
            binding.filters = advertisement.filters

            db.document("users/${advertisement.createdByUserUid!!}")
                .get().addOnSuccessListener {
                    binding.user = it.toObject(User::class.java)
                }


            binding.btnRespond.setOnClickListener {
                listener.onRespond(advertisement.createdByUserUid)
            }

            binding.ivProfilePicture.setOnClickListener {
                listener.onProfilePictureClicked(advertisement.createdByUserUid)
            }

            if (advertisement.description != null) {
                binding.tvShowMore.visibility = View.VISIBLE
                binding.tvShowMore.setOnClickListener {
                    val text = binding.tvShowMore.text
                    if (text == it.resources.getString(R.string.label_show_more)) {
                        binding.tvDescription.visibility = View.VISIBLE
                        binding.tvShowMore.text = it.resources.getString(R.string.label_show_less)
                    } else {
                        binding.tvDescription.visibility = View.GONE
                        binding.tvShowMore.text = it.resources.getString(R.string.label_show_more)
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

    interface OnAdvertisementClickListener {
        fun onRespond(userId: String)
        fun onProfilePictureClicked(userId: String)
    }
}