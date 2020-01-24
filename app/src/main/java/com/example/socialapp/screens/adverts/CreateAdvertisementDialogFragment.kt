package com.example.socialapp.screens.adverts

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.socialapp.R
import com.example.socialapp.databinding.DialogCreateAdvertisementBinding
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import timber.log.Timber


class CreateAdvertisementDialogFragment : DialogFragment() {

    interface NewAdvertisementListener {
        fun onCreateNewAdvertisement(advert: Advertisement)
    }

    private lateinit var binding: DialogCreateAdvertisementBinding
    private lateinit var listener: NewAdvertisementListener

    private var description: String? = null
        get() {
            return if (!binding.etDescription.text.isNullOrEmpty()) {
                binding.etDescription.text.toString()
            } else {
                null
            }
        }

    private var playersNumber: Long? = null
        get() {
            val selected = binding.spinnerPlayersNumber.selectedItem.toString()
            return if (selected.contentEquals(getString(R.string.any_players_number))) {
                null
            } else {
                selected.toLong()
            }
        }

    private var communicationLanguage: String? = null
        get() {
            val selected = binding.spinnerLanguage.selectedItem.toString()
            return if (selected.contentEquals("Any")) {
                null
            } else {
                selected
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateAdvertisementBinding.inflate(inflater, container, false)
        binding.toolbar.navigationIcon!!.setTint(Color.WHITE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { dismiss() }
        binding.btnAddNewPost.setOnClickListener { addNewPost() }

    }

    private fun addNewPost() {
        val newAdvert =
            Advertisement(
                advertisementId = null,
                createdByUserUid = null,
                dateCreated = null,
                filters = Filters(
                    playersNumber = playersNumber,
                    game = binding.spinnerGame.selectedItem.toString(),
                    communicationLanguage = communicationLanguage
                ),
                user = null,
                description = description
            )
        listener.onCreateNewAdvertisement(newAdvert)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as NewAdvertisementListener
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    // Makes dialog display as fully maximized
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.AppTheme_Slide)
        }
    }


}