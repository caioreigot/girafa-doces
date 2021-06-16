package com.github.caioreigot.girafadoces.data.model

object UserSingleton {

    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var deliveryAddress: String = ""
    var isAdmin: Boolean = false

    fun set(user: User) {
        apply {
            fullName = user.fullName
            email = user.email
            phoneNumber = user.phoneNumber
            deliveryAddress = user.deliveryAddress
            isAdmin = user.isAdmin
        }
    }

    fun clear() {
        fullName = ""
        email = ""
        phoneNumber = ""
        deliveryAddress = ""
        isAdmin = false
    }
}