package com.github.caioreigot.girafadoces.data.model

object Global {
    const val PASSWORD_MINIMUM_LENGTH = 6

    object DatabaseNames {
        //region User
        const val USERS_PARENT = "users"
        const val USER_FULL_NAME = "fullName"
        const val USER_EMAIL = "emailAddress"
        const val USER_PHONE = "phoneNumber"
        const val USER_DELIVERY_ADDRESS = "deliveryAddress"
        const val USER_IS_ADMINISTRATOR = "isAdministrator"
        //endregion

        //region Menu Itens
        const val MENU_ITENS_PARENT = "menu_itens"
        const val MENU_ITEM_HEADER = "header"
        const val MENU_ITEM_CONTENT = "content"
        //endregion
    }
}