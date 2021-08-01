package com.github.caioreigot.girafadoces.data.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object Singleton {

    private fun getAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()

    private fun getDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()

    private fun getStorageInstance() = FirebaseStorage.getInstance()

    private fun getDatabaseUsersReference(): DatabaseReference = DATABASE_INSTANCE
        .reference
        .child(Global.DatabaseNames.USERS_PARENT)

    private fun getDatabaseAdminsReference(): DatabaseReference = DATABASE_INSTANCE
        .reference
        .child(Global.DatabaseNames.ADMINS_PARENT)

    private fun getDatabaseMenuItemsReference(): DatabaseReference = DATABASE_INSTANCE
        .reference
        .child(Global.DatabaseNames.MENU_ITEMS_PARENT)

    private fun getDatabaseOrdersReference(): DatabaseReference = DATABASE_INSTANCE
        .reference
        .child(Global.DatabaseNames.ORDERS_PARENT)

    private fun getMenuItemsStorageReference(): StorageReference = FIREBASE_STORAGE_INSTANCE
        .reference
        .child(Global.StorageNames.MENU_IMAGES)

    val AUTH: FirebaseAuth = getAuthInstance()
    private val DATABASE_INSTANCE: FirebaseDatabase = getDatabaseInstance()
    private val FIREBASE_STORAGE_INSTANCE = getStorageInstance()
    val DATABASE_USERS_REF: DatabaseReference = getDatabaseUsersReference()
    val DATABASE_ADMINS_REF: DatabaseReference = getDatabaseAdminsReference()
    val DATABASE_MENU_ITEMS_REF: DatabaseReference = getDatabaseMenuItemsReference()
    val DATABASE_ORDERS_REF: DatabaseReference = getDatabaseOrdersReference()
    val STORAGE_MENU_ITEMS_REF = getMenuItemsStorageReference()
}