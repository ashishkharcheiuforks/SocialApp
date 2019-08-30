package com.example.socialapp.screens.main.menu


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.R
import com.example.socialapp.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth


class MenuFragment : Fragment(), MenuItemListener {
    private lateinit var binding: FragmentMenuBinding

    /**    In order to add an item to the menu add new MenuItem data class object to the menuItems List    **/

    private val menuItems = listOf(
        MenuItem("Sign out", R.drawable.ic_home_black_24dp)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MenuAdapter(menuItems, this)

        binding.menuRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.menuRecyclerview.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    /**    Menu Items on click handle    **/

    override fun onMenuItemClick(menuItem: MenuItem) {
        when(menuItem.menuItemTitle){
            "Sign out" -> signOut()
        }
    }


    private fun signOut(){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }


}
