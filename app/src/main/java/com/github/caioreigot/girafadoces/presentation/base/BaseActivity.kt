package com.github.caioreigot.girafadoces.presentation.base

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    protected fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}