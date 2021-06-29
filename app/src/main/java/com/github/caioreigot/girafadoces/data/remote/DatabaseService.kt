package com.github.caioreigot.girafadoces.data.remote

import android.text.TextUtils
import android.util.Log
import com.github.caioreigot.girafadoces.data.helper.Utils
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.toBitmap
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class DatabaseService @Inject constructor() : DatabaseRepository {

    override fun getLoggedUserInformation(callback: (User?, ServiceResult) -> Unit) {
        Singleton.mAuth.currentUser?.let { currentUser ->
            val loggedUserReference = Singleton.mDatabaseUsersReference.child(currentUser.uid)

            Singleton.mDatabaseAdminsReference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(adminsSnapshot: DataSnapshot) {
                        val isUserAdmin = adminsSnapshot.child(currentUser.uid).exists()

                        loggedUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val user: User? = userSnapshot.getValue(User::class.java)
                                user?.isAdmin = isUserAdmin

                                callback(user, ServiceResult.Success)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                                return
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                        return
                    }
                })

            return
        }

        callback(null, ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
    }

    override fun getUserByUid(uid: String, callback: (User?, serviceResult: ServiceResult) -> Unit) {
        Singleton.mDatabaseUsersReference.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    val user: User? = userSnapshot.getValue(User::class.java)
                    callback(user, ServiceResult.Success)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                    return
                }
            })
    }

    override fun getAdministratorsUsers(
        callback: (MutableList<User>?, serviceResult: ServiceResult) -> Unit
    ) {
        val allAdminsUID = mutableListOf<String>()
        val adminUsers = mutableListOf<User>()

        Singleton.mDatabaseAdminsReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Adding UID of admin users to list
                    for (uidSnapshot in snapshot.children)
                        uidSnapshot.key?.let { key -> allAdminsUID.add(key) }

                    if (allAdminsUID.size == snapshot.childrenCount.toInt()) {
                        Singleton.mDatabaseUsersReference
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    // Getting the information from each admin user, in the users node
                                    for (i in allAdminsUID.indices) {
                                        snapshot.child(allAdminsUID[i]).getValue(User::class.java)
                                            ?.let { user -> adminUsers.add(user) }
                                    }

                                    callback(adminUsers, ServiceResult.Success)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                                    return
                                }
                            })
                    } else
                        callback(null, ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                    return
                }
            })
    }

    override fun getAdministratorUidByEmail(
        email: String,
        callback: (uid: String?, serviceResult: ServiceResult) -> Unit
    ) {
        Singleton.mDatabaseUsersReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        if (email == user.child(Global.DatabaseNames.USER_EMAIL).value) {
                            callback(user.key, ServiceResult.Success)
                            return
                        }
                    }

                    callback(null, ServiceResult.Error(ErrorType.ACCOUNT_NOT_FOUND))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                    return
                }
        })
    }

    override fun changeAccountField(
        accountField: UserAccountField,
        newValue: Any,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        var databaseUserKeyName: String?
        var errorType = ErrorType.UNEXPECTED_ERROR
        var isValid = true

        when (accountField) {
            UserAccountField.NAME -> {
                if (TextUtils.isEmpty(newValue.toString())) {
                    isValid = false
                    errorType = ErrorType.EMPTY_FIELD
                }

                databaseUserKeyName = Global.DatabaseNames.USER_FULL_NAME
            }

            UserAccountField.DELIVERY_ADDRESS -> {
                if (TextUtils.isEmpty(newValue.toString())) {
                    isValid = false
                    errorType = ErrorType.EMPTY_FIELD
                }

                databaseUserKeyName = Global.DatabaseNames.USER_DELIVERY_ADDRESS
            }

            UserAccountField.PHONE -> {
                if (TextUtils.isEmpty(newValue.toString())) {
                    isValid = false
                    errorType = ErrorType.EMPTY_FIELD
                } else if (!Utils.isValidPhoneNumber(newValue.toString(), "(XX) XXXXX-XXXX")) {
                    isValid = false
                    errorType = ErrorType.INVALID_PHONE
                }

                databaseUserKeyName = Global.DatabaseNames.USER_PHONE
            }

            else -> databaseUserKeyName = null
        }

        if (!isValid) {
            callback(ServiceResult.Error(errorType))
            return
        }

        if (Singleton.mAuth.currentUser == null || databaseUserKeyName == null) {
            callback(ServiceResult.Error(ErrorType.SERVER_ERROR))
            return
        }

        val currentUserUid = Singleton.mAuth.currentUser!!.uid

        Singleton.mDatabaseUsersReference
            .child(currentUserUid)
            .child(databaseUserKeyName)
            .setValue(newValue.toString().trimEnd())
            .addOnSuccessListener {
                callback(ServiceResult.Success)

                getLoggedUserInformation { _user, _ ->
                    _user?.let { user -> UserSingleton.set(user) }
                }
            }

            .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }
    }

    override fun getMenuItems(
        storage: StorageRepository,
        callback: (MutableList<MenuItem>?, ServiceResult) -> Unit
    ) {
        storage.downloadAllImages { imageUidAndByteArray, result ->
            when (result) {
                is ServiceResult.Success -> {

                    if (imageUidAndByteArray == null) {
                        callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                        return@downloadAllImages
                    }

                    val menuItemsList = mutableListOf<MenuItem>()
                    val imagesPair: List<Pair<String, ByteArray>> = imageUidAndByteArray

                    fetchDatabaseMenuItemsInformation {
                        it?.let { menuItems ->
                            /* Associating the images with your information
                            previously taken from the database */
                            for (menuItem in menuItems) {
                                for (uidAndByteArray in imagesPair) {
                                    if (menuItem.uid == uidAndByteArray.first) {
                                        menuItem.image = uidAndByteArray.second.toBitmap()
                                        menuItemsList.add(menuItem)
                                    }
                                }
                            }

                            callback(menuItemsList, ServiceResult.Success)
                        }
                    }
                }

                is ServiceResult.Error -> callback(
                    null,
                    ServiceResult.Error(ErrorType.SERVER_ERROR)
                )
            }
        }
    }

    private fun fetchDatabaseMenuItemsInformation(callback: (MutableList<MenuItem>?) -> Unit) {
        Singleton.mDatabaseMenuItensReference.addListenerForSingleValueEvent(object :
            ValueEventListener {

            val itemsList = mutableListOf<MenuItem>()

            override fun onDataChange(snapshot: DataSnapshot) {
                for (menuItem in snapshot.children) {
                    menuItem.key?.let { key ->
                        val item: MenuItem? = menuItem.getValue(MenuItem::class.java)

                        if (item != null) {
                            item.uid = key
                            itemsList.add(item)
                        }
                    }
                }

                callback(itemsList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    override fun saveMenuItem(
        itemHeader: String,
        itemContent: String,
        callback: (uid: String?, ServiceResult) -> Unit
    ) {
        val key: String? = Singleton.mDatabaseMenuItensReference.push().key

        key?.let { uid ->
            with(Singleton.mDatabaseMenuItensReference.child(uid)) {
                child(Global.DatabaseNames.MENU_ITEM_HEADER).setValue(itemHeader)
                child(Global.DatabaseNames.MENU_ITEM_CONTENT).setValue(itemContent)
            }

                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> callback(uid, ServiceResult.Success)
                        false -> callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
                    }
                }
        } ?: callback(null, ServiceResult.Error(ErrorType.SERVER_ERROR))
    }

    override fun removeMenuItem(
        storage: StorageRepository,
        uid: String,
        callback: (serviceResult: ServiceResult) -> Unit
    ) {
        storage.deleteImage(uid) { result ->
            when (result) {
                is ServiceResult.Success -> {
                    Singleton.mDatabaseMenuItensReference.child(uid).removeValue()
                        .addOnSuccessListener { callback(ServiceResult.Success) }
                        .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }
                }

                is ServiceResult.Error -> callback(result)
            }
        }
    }
}