package com.example.socialapp.screens.adverts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
        val selectedLanguage = binding.spinnerLanguage.selectedItem.toString()
        val selectedPlayersNumber = binding.spinnerPlayersNumber.selectedItem.toString()
        val filters = Filters(
            game = binding.spinnerGame.selectedItem.toString(),
            communicationLanguage = if (selectedLanguage.contentEquals("Any Language")) {
                null
            } else selectedLanguage,
            playersNumber = if (selectedPlayersNumber.contentEquals("Any Players Amount"))
                null
            else
                selectedPlayersNumber.toLong()
        )
        return filters
    }

}