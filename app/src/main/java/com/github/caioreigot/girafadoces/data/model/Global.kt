package com.github.caioreigot.girafadoces.data.model

object Global {

    const val AUTH_TIME_OUT_IN_MILLIS = 10000L
    const val PASSWORD_MINIMUM_LENGTH = 6

    object DatabaseNames {
        //region users
        const val USERS_PARENT = "users"
        const val USER_FULL_NAME = "fullName"
        const val USER_EMAIL = "emailAddress"
        const val USER_PHONE = "phoneNumber"
        const val USER_DELIVERY_ADDRESS = "deliveryAddress"
        //endregion

        //region menu_itens
        const val MENU_ITEMS_PARENT = "menu_itens"
        const val MENU_ITEM_HEADER = "header"
        const val MENU_ITEM_CONTENT = "content"
        //endregion

        //region admins
        const val ADMINS_PARENT = "admins"
        //endregion
    }

    object StorageNames {
        const val MENU_IMAGES = "menu_images"
    }
}