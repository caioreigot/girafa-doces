package com.github.caioreigot.girafadoces.data.model

import com.google.firebase.database.PropertyName

data class User (
    @get:PropertyName(Global.DatabaseNames.USER_FULL_NAME)
    @set:PropertyName(Global.DatabaseNames.USER_FULL_NAME)
    var fullName: String = "",

    @get:PropertyName(Global.DatabaseNames.USER_EMAIL)
    @set:PropertyName(Global.DatabaseNames.USER_EMAIL)
    var email: String = "",

    @get:PropertyName(Global.DatabaseNames.USER_PHONE)
    @set:PropertyName(Global.DatabaseNames.USER_PHONE)
    var phoneNumber: String = "",

    @get:PropertyName(Global.DatabaseNames.USER_DELIVERY_ADDRESS)
    @set:PropertyName(Global.DatabaseNames.USER_DELIVERY_ADDRESS)
    var deliveryAddress: String = "",

    @get:PropertyName(Global.DatabaseNames.USER_IS_ADMINISTRATOR)
    @set:PropertyName(Global.DatabaseNames.USER_IS_ADMINISTRATOR)
    var isAdministrator: Boolean = false
)

class Builder {
    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var deliveryAddress: String = ""
    var isAdministrator: Boolean = false

    fun build(): User {
        return User(fullName, email, phoneNumber, deliveryAddress, isAdministrator)
    }
}

fun user(block: Builder.() -> Unit): User {
    return Builder().apply(block).build()
}


