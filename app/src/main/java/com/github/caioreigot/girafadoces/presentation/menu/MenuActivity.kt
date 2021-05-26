package com.github.caioreigot.girafadoces.presentation.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.model.UserSingleton

class MenuActivity : AppCompatActivity() {

    lateinit var menuTestTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        Log.d("MY_DEBUG", Singleton.mFirebaseAuth.currentUser?.uid.toString())
        Log.d("MY_DEBUG", UserSingleton.fullName)
        Log.d("MY_DEBUG", UserSingleton.deliveryAddress)
        Log.d("MY_DEBUG", UserSingleton.phoneNumber)
        Log.d("MY_DEBUG", UserSingleton.email)

        menuTestTV = findViewById(R.id.menu_test_tv)
        menuTestTV.text = UserSingleton.fullName
    }
}