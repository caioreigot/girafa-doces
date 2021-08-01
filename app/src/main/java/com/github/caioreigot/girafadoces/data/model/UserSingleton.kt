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

    fun getUserObject(): User =
        user {
            fullName = UserSingleton.fullName
            email = UserSingleton.email
            phoneNumber = UserSingleton.phoneNumber
            deliveryAddress = UserSingleton.deliveryAddress
            isAdmin = UserSingleton.isAdmin
        }

    fun clear() {
        fullName = ""
        email = ""
        phoneNumber = ""
        deliveryAddress = ""
        isAdmin = false
    }
}