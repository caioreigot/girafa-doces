package com.github.caioreigot.girafadoces.data.remote.database

import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.Utils.Companion.toBitmap
import com.github.caioreigot.girafadoces.data.model.Singleton
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DatabaseDataSource : DatabaseRepository {

    override fun getLoggedUserInformation(callback: (User?, FirebaseResult) -> Unit) {
        Singleton.mAuth.currentUser?.let { currentUser ->
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

    override fun getMenuItems(
        storageSource: StorageRepository,
        callback: (MutableList<MenuItem>?, FirebaseResult) -> Unit
    ) {
        storageSource.downloadAllImages { imageUidAndByteArray, result ->
            when (result) {
                is FirebaseResult.Success -> {

                    if (imageUidAndByteArray == null) {
                        callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
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

                            callback(menuItemsList, FirebaseResult.Success)
                        }
                    }
                }

                is FirebaseResult.Error -> callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
            }
        }
    }

    private fun fetchDatabaseMenuItemsInformation(callback: (MutableList<MenuItem>?) -> Unit) {
        Singleton.mDatabaseMenuItensReference.addListenerForSingleValueEvent(object : ValueEventListener {

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
        callback: (uid: String?, FirebaseResult) -> Unit
    ) {
        val key: String? = Singleton.mDatabaseMenuItensReference.push().key

        key?.let { uid ->
            with(Singleton.mDatabaseMenuItensReference.child(uid)) {
                child(Global.DatabaseNames.MENU_ITEM_HEADER).setValue(itemHeader)
                child(Global.DatabaseNames.MENU_ITEM_CONTENT).setValue(itemContent)
            }.addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> callback(uid, FirebaseResult.Success)
                    false -> callback(null, FirebaseResult.Error(ErrorType.SERVER_ERROR))
                }
            }
        }
    }

    override fun removeMenuItem(
        storageSource: StorageRepository,
        uid: String,
        callback: (result: FirebaseResult) -> Unit
    ) {
        storageSource.deleteImage(uid) { result ->
            when (result) {
                is FirebaseResult.Success -> {
                    Singleton.mDatabaseMenuItensReference.child(uid).removeValue()
                        .addOnSuccessListener { callback(FirebaseResult.Success) }
                        .addOnFailureListener { callback(FirebaseResult.Error(ErrorType.SERVER_ERROR)) }
                }

                is FirebaseResult.Error -> callback(result)
            }
        }
    }
}