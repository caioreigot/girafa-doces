package com.github.caioreigot.girafadoces.data.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object Singleton {
    // FirebaseAuth
    private fun getAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()
    val mAuth: FirebaseAuth = getAuthInstance()

    // Database Instance
    private fun getDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()
    val mDatabase: FirebaseDatabase = getDatabaseInstance()

    // Database Users Reference
    private fun getDatabaseUsersReference(): DatabaseReference = mDatabase
        .reference
        .child(Global.DatabaseNames.USERS_PARENT)

    val mDatabaseUsersReference: DatabaseReference = getDatabaseUsersReference()

    // Database Menu Itens Reference
    private fun getDatabaseMenuItensReference(): DatabaseReference = mDatabase
        .reference
        .child(Global.DatabaseNames.MENU_ITENS_PARENT)

    val mDatabaseMenuItensReference: DatabaseReference = getDatabaseMenuItensReference()

    // Storage Instance
    private fun getStorageInstance() = FirebaseStorage.getInstance()
    val mFirebaseStorage = getStorageInstance()

    // Storage Reference
    private fun getMenuItensStorageReference(): StorageReference = mFirebaseStorage
        .reference
        .child("menu_images")

    val mStorageMenuImagesReference = getMenuItensStorageReference()
}