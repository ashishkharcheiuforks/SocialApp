package com.example.socialapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment
import com.example.socialapp.common.hideKeyboard
import com.example.socialapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class MainActivity : AppCompatActivity(), ViewModelStoreOwner {

    private lateinit var binding: ActivityMainBinding

    private val auth = FirebaseAuth.getInstance()

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // LiveData listening for auth state change
        viewModel.authLiveData.observe(this, Observer { authState ->
            // If user becomes unauthenticated
            // and current screen is not login/register screen
            // then redirect him to login screen
            if (authState == null) {
                val navHost =
                    supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
                val navController = navHost.navController
                val currentFragmentId = navController.currentDestination?.id
                if (currentFragmentId != R.id.loginFragment || currentFragmentId != R.id.registerFragment) {
                    navController.navigate(R.id.action_global_loginFragment)
                }
            }
        })
        setStartDestination()

    }

    // Inflates main_nav_graph.xml and sets proper start destination depending on auth state
    private fun setStartDestination() {
        Timber.d("setStartDestination() called")
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.main_nav_graph)

        if (isUserSignedin()) {
            graph.startDestination = R.id.authenticated_graph
        } else {
            graph.startDestination = R.id.loginFragment
        }
        navController.graph = graph

        // Ensures intercepting system Back button
        // Works like classic app:defaultNavHost="" in activity_main.xml
        // which had to be deleted due to setting start destination programmatically in this activity
        supportFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHost).commit()
    }

    private fun isUserSignedin(): Boolean = auth.currentUser?.uid != null

}