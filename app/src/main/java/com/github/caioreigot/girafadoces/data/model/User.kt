package com.github.caioreigot.girafadoces.data.model

import com.google.firebase.database.PropertyName

object UserSingleton {
    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var deliveryAddress: String = ""

    fun set(user: User) {
        apply {
            fullName = user.fullName
            email = user.email
            phoneNumber = user.phoneNumber
            deliveryAddress = user.deliveryAddress
        }
    }
}

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
    var deliveryAddress: String = ""
)

class Builder {
    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var deliveryAddress: String = ""

    fun build(): User {
        return User(fullName, email, phoneNumber, deliveryAddress)
    }
}

fun user(block: Builder.() -> Unit): User {
    return Builder().apply(block).build()
}


