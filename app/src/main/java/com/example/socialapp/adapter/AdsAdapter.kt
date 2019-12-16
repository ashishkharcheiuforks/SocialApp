package com.example.socialapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemAdvertBinding
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

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
                .get().addOnCompleteListener {task ->
                if(task.isSuccessful){
                    binding.user = User(
                        profilePictureUri = Uri.parse(task.result!!["profilePictureUrl"] as String),
                        nickname = task.result!!["nickname"] as String,
                        firstName = task.result!!["firstName"] as String,
                        uid = task.result!!.id
                    )
                }else{
                    Timber.e(task.exception)
                }
            }

            binding.btnRespond.setOnClickListener {
                listener.onRespond(advertisement.createdByUserUid)
            }

            binding.ivProfilePicture.setOnClickListener {
                listener.onProfilePictureClicked(advertisement.createdByUserUid)
            }

            binding.executePendingBindings()
        }
    }

    interface OnAdvertisementClickListener {
        fun onRespond(userId: String)
        fun onProfilePictureClicked(userId: String)
    }
}