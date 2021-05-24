package com.github.caioreigot.girafadoces.data.model

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    var phoneNumber: String,
    var postalAddress: String
)
