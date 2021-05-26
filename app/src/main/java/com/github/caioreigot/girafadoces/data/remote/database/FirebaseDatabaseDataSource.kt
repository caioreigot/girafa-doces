package com.github.caioreigot.girafadoces.data.remote.database

import android.util.Log
import com.github.caioreigot.girafadoces.data.FirebaseResult
import com.github.caioreigot.girafadoces.data.Singleton
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Global
import com.github.caioreigot.girafadoces.data.model.User
import com.github.caioreigot.girafadoces.data.model.user
import com.github.caioreigot.girafadoces.data.repository.FirebaseDatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseDataSource : FirebaseDatabaseRepository {

    override fun getLoggedUserInformation(callback: (User?, FirebaseResult) -> Unit) {
        Singleton.mFirebaseAuth.currentUser?.let { currentUser ->
            val loggedUserReference = Singleton.mDatabaseUsersReference.child(currentUser.uid)

            loggedUserReference.addListenerForSingleValueEvent(object : ValueEventListener {

                // User Data Class Fields
                var dbFullName: String? = ""
                var dbEmail: String? = ""
                var dbPhoneNumber: String? = ""
                var dbDeliveryAddress: String? = ""

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userInformation in snapshot.children) {
                        dbFullName = if (userInformation.key == Global.DatabaseNames.USER_FULL_NAME)
                            userInformation.getValue(String::class.java) else dbFullName

                        dbEmail = if (userInformation.key == Global.DatabaseNames.USER_EMAIL)
                            userInformation.getValue(String::class.java) else dbEmail

                        dbPhoneNumber = if (userInformation.key == Global.DatabaseNames.USER_PHONE)
                            userInformation.getValue(String::class.java) else dbPhoneNumber

                        dbDeliveryAddress = if (userInformation.key == Global.DatabaseNames.USER_DELIVERY_ADDRESS)
                            userInformation.getValue(String::class.java) else dbDeliveryAddress
                    }

                    callback(user {
                        fullName = dbFullName ?: ""
                        email = dbEmail ?: ""
                        phoneNumber = dbPhoneNumber ?: ""
                        deliveryAddress = dbDeliveryAddress ?: ""
                    }, FirebaseResult.Success)
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