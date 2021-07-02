package com.github.caioreigot.girafadoces.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.local.Preferences
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.UserSingleton
import com.github.caioreigot.girafadoces.ui.base.BaseActivity
import com.github.caioreigot.girafadoces.ui.login.LoginActivity
import com.github.caioreigot.girafadoces.ui.main.menu.admin_menu.AdminMenuDialog
import com.github.caioreigot.girafadoces.ui.main.menu.MenuFragment
import com.github.caioreigot.girafadoces.ui.main.account.AccountFragment
import com.github.caioreigot.girafadoces.ui.main.admin.AdminPanelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavActivity : BaseActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val fragmentManager = supportFragmentManager

    private val menuFragment = MenuFragment()
    private val accountFragment = AccountFragment()
    private val addFragment = AdminMenuDialog()
    private val adminPanelFragment = AdminPanelFragment()

    private var activeFragment: Fragment = menuFragment

    companion object {
        private const val MENU_FRAGMENT_TAG = "menu_fragment"
        private const val ACCOUNT_FRAGMENT_TAG = "account_fragment"
        private const val ADD_FRAGMENT_TAG = "add_fragment"
        private const val ADMIN_PANEL_TAG = "admin_panel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assignments
        bottomNavigation = findViewById(R.id.main_bottom_navigation)

        bottomNavigation.menu.clear()

        bottomNavigation.inflateMenu(
            if (UserSingleton.isAdmin)
                R.menu.administrator_menu_items
            else
                R.menu.user_menu_items
        )

        fragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, menuFragment, MENU_FRAGMENT_TAG).hide(menuFragment)
            add(R.id.fragment_container, accountFragment, ACCOUNT_FRAGMENT_TAG).hide(accountFragment)
            add(R.id.fragment_container, addFragment, ADD_FRAGMENT_TAG).hide(addFragment)
            add(R.id.fragment_container, adminPanelFragment, ADMIN_PANEL_TAG).hide(adminPanelFragment)
        }.commit()

        // Default Fragment
        fragmentManager.beginTransaction().show(activeFragment).commit()

        //region Listeners
        bottomNavigation.setOnNavigationItemSelectedListener { selectedItem ->
            when (selectedItem.itemId) {
                R.id.menu -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(menuFragment).commit()
                    activeFragment = menuFragment
                    true
                }

                R.id.user_profile -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(accountFragment).commit()
                    activeFragment = accountFragment
                    true
                }

//                R.id.add_menu_item -> {
//                    fragmentManager.beginTransaction().hide(activeFragment).show(addFragment).commit()
//                    activeFragment = addFragment
//                    true
//                }

                R.id.admin_panel -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(adminPanelFragment).commit()
                    activeFragment = adminPanelFragment
                    true
                }

                else -> false
            }
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
        @StringRes header: Int,
        @StringRes content: Int,
        positiveOnClickCallback: (() -> Unit)? = null,
        negativeOnClickCallback: (() -> Unit)? = null,
        callback: ((choice: Boolean) -> Unit)? = null
    ): Dialog {
        return createMessageDialog(
            this,
            messageType,
            getString(header),
            getString(content),
            positiveOnClickCallback,
            negativeOnClickCallback,
            callback
        )
    }

    fun showMessageDialog(
        messageType: MessageType,
        @StringRes header: Int,
        content: String,
        positiveOnClickCallback: (() -> Unit)? = null,
        negativeOnClickCallback: (() -> Unit)? = null,
        callback: ((choice: Boolean) -> Unit)? = null
    ): Dialog {
        return createMessageDialog(
            this,
            messageType,
            getString(header),
            content,
            positiveOnClickCallback,
            negativeOnClickCallback,
            callback
        )
    }
}