package com.github.caioreigot.girafadoces.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.presentation.base.BaseActivity
import com.github.caioreigot.girafadoces.presentation.main.add.AddFragment
import com.github.caioreigot.girafadoces.presentation.main.menu.MenuFragment
import com.github.caioreigot.girafadoces.presentation.main.account.AccountFragment
import com.github.caioreigot.girafadoces.presentation.main.admin.AdminFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assignments
        bottomNavigation = findViewById(R.id.main_bottom_navigation)

        //showDebugUserInfo()

        bottomNavigation.menu.clear()

        bottomNavigation.inflateMenu(
            if (UserSingleton.isAdmin)
                R.menu.administrator_menu_items
            else
                R.menu.user_menu_items
        )

        val selectedFragment: Fragment = when (bottomNavigation.selectedItemId) {
            R.id.menu -> MenuFragment()
            R.id.user_profile -> AccountFragment()
            R.id.add_menu_item -> AddFragment()

            else -> MenuFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commit()

        // Listeners
        bottomNavigation.setOnNavigationItemSelectedListener { selectedItem ->

            if (bottomNavigation.selectedItemId == selectedItem.itemId)
                return@setOnNavigationItemSelectedListener false

            val _selectedFragment: Fragment = when (selectedItem.itemId) {
                R.id.menu -> MenuFragment()
                R.id.user_profile -> AccountFragment()
                R.id.add_menu_item -> AddFragment()
                R.id.admin_panel -> AdminFragment()

                else -> MenuFragment()
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, _selectedFragment)
                .commit()

            return@setOnNavigationItemSelectedListener true
        }
    }

    fun showMessageDialog(
        messageType: MessageType,
        @StringRes stringRes: Int,
        errorMessage: String,
        positiveOnClickListener: (() -> Unit)? = null,
        negativeOnClickListener: (() -> Unit)? = null,
        callback: ((choice: Boolean) -> Unit)? = null
    ) {
        createMessageDialog(
            this,
            messageType,
            getString(stringRes),
            errorMessage,
            positiveOnClickListener,
            negativeOnClickListener,
            callback
        ).show()
    }

    fun showDebugUserInfo() {
        Log.d("MY_DEBUG", Singleton.mAuth.currentUser?.uid.toString())
        Log.d("MY_DEBUG", UserSingleton.fullName)
        Log.d("MY_DEBUG", UserSingleton.deliveryAddress)
        Log.d("MY_DEBUG", UserSingleton.phoneNumber)
        Log.d("MY_DEBUG", UserSingleton.email)
        Log.d("MY_DEBUG", UserSingleton.isAdmin.toString())
    }
}