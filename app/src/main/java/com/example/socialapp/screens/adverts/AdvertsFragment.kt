package com.example.socialapp.screens.main.adverts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.socialapp.databinding.FragmentAdvertsBinding
import timber.log.Timber


class AdvertsFragment : Fragment() {

    private lateinit var binding: FragmentAdvertsBinding

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

        val viewModel: AdvertsViewModel by viewModels()

//        viewModel.text.observe(this, Observer {
//            Timber.i(it.toString())
//        })

//        binding.btnAddNewAdvert.setOnClickListener {
//            NewAdvertisementDialogFragment(viewModel).show(
////                activity!!.supportFragmentManager,
//                childFragmentManager,
//                "new_advert_dialog"
//            )
//        }


    }
}
