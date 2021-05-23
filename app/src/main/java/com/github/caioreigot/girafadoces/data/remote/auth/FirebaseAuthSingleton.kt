package com.github.caioreigot.girafadoces.data.remote.auth

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthSingleton {
    private fun getFirebaseAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()
    val mFirebaseAuth: FirebaseAuth = getFirebaseAuthInstance()
}