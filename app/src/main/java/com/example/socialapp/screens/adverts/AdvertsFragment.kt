package com.example.socialapp.screens.adverts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.example.socialapp.adapter.AdsAdapter
import com.example.socialapp.databinding.FragmentAdvertsBinding
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import com.example.socialapp.screens.main.MainScreenFragmentDirections
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import timber.log.Timber


class AdvertsFragment : Fragment(),
    FilterDialogFragment.FilterListener,
    NewAdvertisementDialogFragment.NewAdvertisementListener,
//    AdvertsAdapter.OnAdvertisementClickListener
    AdsAdapter.OnAdvertisementClickListener {

    private lateinit var binding: FragmentAdvertsBinding

    private val viewModel: AdvertsViewModel by viewModels()

    private lateinit var dupapter: AdsAdapter

//    private lateinit var adapter:AdvertsAdapter
//    private val adapter by lazy { AdvertsAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdvertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnAddNewAdvert.setOnClickListener { openNewAdvertisementDialog() }
            filterBar.setOnClickListener { openFilterDialog() }
            btnClearFilters.setOnClickListener { clearFilter() }
        }

        initTurboShitRecyclerv2(Filters())

//        adapter = AdvertsAdapter(this)
//        binding.recyclerview.adapter = adapter
//
//        viewModel.adverts.observe(viewLifecycleOwner, Observer {
//            adapter.submitList(it)
//
//        })
    }

    private fun openNewAdvertisementDialog() {
        val dialog =
            NewAdvertisementDialogFragment()
        dialog.show(childFragmentManager, "new_advert_dialog")
    }

    private fun openFilterDialog() {
        val dialogFragment =
            FilterDialogFragment()
        dialogFragment.show(childFragmentManager, "filter_dialog")
    }

    private fun clearFilter() {
        if(viewModel.filters.value!! != Filters()) {
            viewModel.filters.value = Filters()
            initTurboShitRecyclerv2(viewModel.filters.value!!)
        }
    }

    override fun onFilter(filters: Filters?) {
        if (viewModel.filters.value != filters) {
            viewModel.filters.value = filters!!
//            viewModel.refreshAdverts()
        }
        initTurboShitRecyclerv2(filters!!)
    }

    override fun onCreateNewAdvertisement(advert: Advertisement) {
        viewModel.createNewAdvert(advert).addOnCompleteListener { task ->
            val message: String = if (task.isSuccessful) {
                "Advertisement had been added!"
            } else {
                "Failed to add the advert, try again"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRespond(userId: String) {
        Toast.makeText(context, "TODO: Implement", Toast.LENGTH_LONG).show()
    }

    override fun onProfilePictureClicked(userId: String) {
        navigateToUserProfile(userId)
    }

    private fun navigateToUserProfile(userId: String) {
        val action = MainScreenFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(action)
    }


    private fun initTurboShitRecyclerv2(filters: Filters) {
        var query = FirebaseFirestore.getInstance().collection("advertisements")
            .orderBy("dateCreated", Query.Direction.DESCENDING)
        Timber.d("Filter w dupodapterze: $filters")
        if (!filters.game.isNullOrEmpty()) {
            Timber.d("game filter inside repo set")
            query = query.whereEqualTo("game", filters.game)
        }

        filters.communicationLanguage?.let { language ->
            Timber.d("language filter inside repo set")
            query = query.whereEqualTo(
                "communicationLanguage",
                language
            )
        }
        filters.playersNumber?.let { playersNum ->
            Timber.d("players number filter inside repo set")
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
//                        user = FirestoreRepository().getUser(it.getString("createdByUserUid")!!),
                        user = null,
                        createdByUserUid = it.getString("createdByUserUid")
                    )
                }
                .build()

        dupapter = AdsAdapter(this, options)
        binding.recyclerview.adapter = dupapter
        dupapter.notifyDataSetChanged()

    }

}
