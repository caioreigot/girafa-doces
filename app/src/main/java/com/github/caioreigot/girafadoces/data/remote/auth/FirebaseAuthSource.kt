package com.github.caioreigot.girafadoces.data.remote.auth

import android.text.TextUtils
import com.github.caioreigot.girafadoces.data.Utils
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthSource : FirebaseAuthRepository {

    lateinit var mAuth: FirebaseAuth

    override fun loginUser(
        email: String,
        password: String,
        callback: (loginStatus: Boolean, errorMessage: String?) -> Unit
    ) {
        mAuth = FirebaseAuth.getInstance()

        if (Utils.isValidEmail(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                callback(task.isSuccessful, null)
            }
        } else
            callback(false, "E-mail ou senha inválidos!")
    }
}