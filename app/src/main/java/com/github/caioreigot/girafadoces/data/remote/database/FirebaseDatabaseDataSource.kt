package com.github.caioreigot.girafadoces.data.remote.database

import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.data.repository.FirebaseDatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseDataSource : FirebaseDatabaseRepository {

    override fun getLoggedUserInformation(callback: (User?, FirebaseResult) -> Unit) {
        Singleton.mFirebaseAuth.currentUser?.let { currentUser ->
            val loggedUserReference = Singleton.mDatabaseUsersReference.child(currentUser.uid)

            loggedUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)
                    callback(user, FirebaseResult.Success)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
                    return
                }
            })

            return
        }

        callback(null, FirebaseResult.Error(ErrorType.UNEXPECTED_ERROR))
    }
}