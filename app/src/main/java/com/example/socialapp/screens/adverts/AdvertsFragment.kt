package com.example.socialapp.screens.adverts


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.example.socialapp.R
import com.example.socialapp.adapter.AdsAdapter
import com.example.socialapp.common.showToast
import com.example.socialapp.databinding.FragmentAdvertsBinding
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import com.example.socialapp.screens.main.MainScreenFragmentDirections
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AdvertsFragment : Fragment(),
    FilterDialogFragment.FilterListener,
    CreateAdvertisementDialogFragment.NewAdvertisementListener,
    AdsAdapter.OnAdvertisementClickListener {

    private lateinit var binding: FragmentAdvertsBinding

    private lateinit var adapter: AdsAdapter

    private val viewModel: AdvertsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdvertsBinding.inflate(inflater, container, false)
        // Set colors of Swipe To Refresh Layout Widget
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(Color.WHITE)
            setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(context!!.applicationContext, R.color.colorPrimary)
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnAddNewAdvert.setOnClickListener { openNewAdvertisementDialog() }
            filterBar.setOnClickListener { openFilterDialog() }
            btnClearFilters.setOnClickListener { clearFilter() }
        }

        initRecyclerview(viewModel.filters.value!!)

        binding.swipeRefreshLayout.setOnRefreshListener {
            initRecyclerview(viewModel.filters.value!!)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun openNewAdvertisementDialog() {
        CreateAdvertisementDialogFragment().show(childFragmentManager, "new_advert_dialog")
    }

    private fun openFilterDialog() {
        FilterDialogFragment().show(childFragmentManager, "filter_dialog")
    }

    private fun clearFilter() {
        if (viewModel.filters.value!! != Filters()) {
            viewModel.filters.value = Filters()
            initRecyclerview(viewModel.filters.value!!)
        }
    }

    override fun onFilter(filters: Filters?) {
        if (viewModel.filters.value != filters) {
            viewModel.filters.value = filters!!
            initRecyclerview(filters)
        }
    }

    override fun onCreateNewAdvertisement(advert: Advertisement) {
        viewModel.createNewAdvert(advert)
    }

    override fun onRespond(userId: String) {
        navigateToConversationScreen(userId)
    }

    override fun onProfilePictureClicked(userId: String) {
        navigateToUserProfile(userId)
    }

    private fun navigateToUserProfile(userId: String) {
        val action = MainScreenFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(action)
    }

    private fun navigateToConversationScreen(userId: String){
        val action = MainScreenFragmentDirections.actionGlobalConversationFragment(userId)
        findNavController().navigate(action)
    }

    private fun initRecyclerview(filters: Filters) {
        val db = FirebaseFirestore.getInstance()

        var query = db.collection("advertisements")
            .orderBy("dateCreated", Query.Direction.DESCENDING)

        filters.game?.let { game -> query = query.whereEqualTo("game", game) }
        filters.communicationLanguage?.let { language ->
            query = query.whereEqualTo("communicationLanguage", language)
        }
        filters.playersNumber?.let { playersNum ->
            query = query.whereEqualTo("playersNumber", playersNum)
        }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(20)
            .build()

        val options =
            FirestorePagingOptions.Builder<Advertisement>()
                .setLifecycleOwner(this)
                .setQuery(query, config) {
                    Advertisement(
                        advertisementId = it.id,
                        filters = Filters(
                            game = it.getString("game"),
                            communicationLanguage = it.getString("communicationLanguage"),
                            playersNumber = it.getLong("playersNumber")
                        ),
                        description = it.getString("description"),
                        dateCreated = it.getTimestamp("dateCreated")!!,
                        // Set inside viewholder
                        user = null,
                        createdByUserUid = it.getString("createdByUserUid")
                    )
                }
                .build()

        adapter = AdsAdapter(this, options)
        binding.recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}
