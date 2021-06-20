package com.github.caioreigot.girafadoces.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.ui.base.BaseActivity
import com.github.caioreigot.girafadoces.ui.login.LoginActivity
import com.github.caioreigot.girafadoces.ui.main.add.AddFragment
import com.github.caioreigot.girafadoces.ui.main.menu.MenuFragment
import com.github.caioreigot.girafadoces.ui.main.account.AccountFragment
import com.github.caioreigot.girafadoces.ui.main.admin.AdminPanelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

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
            R.id.admin_panel -> AdminPanelFragment()

            else -> MenuFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commit()

        //region Listeners
        bottomNavigation.setOnNavigationItemSelectedListener { selectedItem ->

            if (bottomNavigation.selectedItemId == selectedItem.itemId)
                return@setOnNavigationItemSelectedListener false

            val _selectedFragment: Fragment = when (selectedItem.itemId) {
                R.id.menu -> MenuFragment()
                R.id.user_profile -> AccountFragment()
                R.id.add_menu_item -> AddFragment()
                R.id.admin_panel -> AdminPanelFragment()

                else -> MenuFragment()
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, _selectedFragment)
                .commit()

            return@setOnNavigationItemSelectedListener true
        }

        // Responsible for logging out the player and taking it to the login screen
        Singleton.mAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                Preferences(this).clearPreferences()
                UserSingleton.clear()
                startActivity(intent)
                finish()
            }
        }
        //endregion
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