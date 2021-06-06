package com.github.caioreigot.girafadoces.data.repository

import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.User

interface DatabaseRepository {
    fun getLoggedUserInformation(
        callback: (User?, result: FirebaseResult) -> Unit
    )

    fun getMenuItems(
        storageSource: StorageRepository,
        callback: (MutableList<MenuItem>?, result: FirebaseResult) -> Unit
    )

    fun saveMenuItem(
        itemHeader: String,
        itemContent: String,
        callback: (uid: String?, result: FirebaseResult) -> Unit
    )

    fun removeMenuItem(
        storageSource: StorageRepository,
        uid: String,
        callback: (result: FirebaseResult) -> Unit
    )
}