package com.github.caioreigot.girafadoces.data.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Singleton {
    // FirebaseAuth
    private fun getAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()
    val mFirebaseAuth: FirebaseAuth = getAuthInstance()

    // Database Instance
    private fun getDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()
    val mDatabase: FirebaseDatabase = getDatabaseInstance()

    // Database Users Reference
    private fun getDatabaseUsersReference(): DatabaseReference = mDatabase
        .reference
        .child(Global.DatabaseNames.USERS_PARENT)

    val mDatabaseUsersReference: DatabaseReference = getDatabaseUsersReference()
}