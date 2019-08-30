package com.example.socialapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment
import com.example.socialapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), ViewModelStoreOwner {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val viewmodel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //If user becomes unauthenticated and the current screen is not login or register screen
        // then login screen is being launched and back stack is being cleared

        viewmodel.authLiveData.observe(this, Observer { authState ->
            if (authState == null) {

                val navHost =
                    supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
                val navController = navHost.navController

                if (navController.currentDestination?.id != R.id.loginFragment
                    || navController.currentDestination?.id != R.id.registerFragment
                ) {
                    navController.navigate(R.id.action_global_loginFragment)
                }
            }
        })

        auth = FirebaseAuth.getInstance()

        setStartDestination(auth)

    }

    /**  Sets Start Destination of main_nav_graph depending on authentication state  **/

    private fun setStartDestination(auth: FirebaseAuth) {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.main_nav_graph)

        if (isUserSignedin(auth)) {
            graph.startDestination = R.id.mainFragment
        } else {
            graph.startDestination = R.id.loginFragment
        }
        navController.graph = graph

        // Ensures intercepting system Back button
        // Works like classic app:defaultNavHost="" in activity_main.xml
        // which had to be deleted due to setting start destination programmatically here
        supportFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHost).commit()
    }

    private fun isUserSignedin(auth: FirebaseAuth): Boolean {
        return auth.currentUser?.uid != null
    }
}