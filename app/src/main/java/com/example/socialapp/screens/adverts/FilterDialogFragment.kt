package com.example.socialapp.screens.adverts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.socialapp.R
import com.example.socialapp.databinding.DialogFiltersBinding
import com.example.socialapp.model.Filters
import timber.log.Timber

class FilterDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFiltersBinding

    private lateinit var listener: FilterListener

    interface FilterListener {
        fun onFilter(filters: Filters?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonApply.setOnClickListener { applyFilters() }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as FilterListener
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun applyFilters() {
        listener.onFilter(getFilters())
        dismiss()
    }

    private fun getFilters(): Filters {
        return Filters(
            game = getSelectedGame(),
            communicationLanguage = getSelectedLanguage(),
            playersNumber = getSelectedPlayersNumber()
        )
    }

    private fun getSelectedGame(): String? {
        return binding.spinnerGame.selectedItem.toString()
    }

    private fun getSelectedLanguage(): String? {
        val selected = binding.spinnerLanguage.selectedItem.toString()
        return if (selected.contentEquals("Any Language")) {
            null
        } else {
            selected
        }
    }

    private fun getSelectedPlayersNumber(): Long? {
        val selected = binding.spinnerPlayersNumber.selectedItem.toString()
        return if (selected.contentEquals(getString(R.string.any_players_number))) {
            null
        }
        else {
            selected.toLong()
        }
    }

}