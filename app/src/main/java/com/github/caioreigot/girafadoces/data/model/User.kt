package com.github.caioreigot.girafadoces.data.model

object UserSingleton {
    var uid: String = ""
    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var postalAddress: String = ""

    fun set(user: User) {
        apply {
            uid = user.uid
            fullName = user.uid
            email = user.uid
            phoneNumber = user.phoneNumber
            postalAddress = user.postalAddress
        }
    }
}

data class User (
    val uid: String,
    val fullName: String,
    val email: String,
    var phoneNumber: String,
    var postalAddress: String
)

class Builder {
    val uid: String = ""
    val fullName: String = ""
    val email: String = ""
    var phoneNumber: String = ""
    var postalAddress: String = ""

    fun build(): User {
        return User(uid, fullName, email, phoneNumber, postalAddress)
    }
}

fun user(block: Builder.() -> Unit): User {
    return Builder().apply(block).build()
}


