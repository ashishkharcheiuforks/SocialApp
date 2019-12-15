package com.example.socialapp.screens.userprofile


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialapp.databinding.DialogCancelFriendInviteBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber


class CancelSentInviteDialog :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogCancelFriendInviteBottomSheetBinding
    private lateinit var listener: ModalBottomSheetListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("onAttach(context) called")

        // Attach interface of parentFragment for callbacks
        try {
            listener = parentFragment as ModalBottomSheetListener
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DialogCancelFriendInviteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetItemCancelFriendInvite.setOnClickListener {
            listener.onDeleteInvitation()
            dismiss()
        }
    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
    }
}
