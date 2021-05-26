package com.github.caioreigot.girafadoces.data.model

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
    val fullName: String,
    val email: String,
    var phoneNumber: String,
    var deliveryAddress: String
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


