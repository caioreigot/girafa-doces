package com.github.caioreigot.girafadoces.data.model

object Global {

    const val AUTH_TIME_OUT_IN_MILLIS = 10000L
    const val PASSWORD_MINIMUM_LENGTH = 6

    object DatabaseNames {
        /*
        girafa-doces-default-rtdb {
            admins { ... },
            menu_items { ... },
            users { ... },
            orders { ... }
        }
        */

        //region users
        const val USERS_PARENT = "users"

        const val USER_FULL_NAME = "fullName"
        const val USER_EMAIL = "emailAddress"
        const val USER_PHONE = "phoneNumber"
        const val USER_DELIVERY_ADDRESS = "deliveryAddress"
        //endregion

        //region menu_items
        const val MENU_ITEMS_PARENT = "menu_items"

        const val MENU_ITEM_HEADER = "header"
        const val MENU_ITEM_CONTENT = "content"
        //endregion

        //region admins
        const val ADMINS_PARENT = "admins"
        //endregion

        //region orders
        const val ORDERS_PARENT = "orders"

        const val ORDER_USER_JSON = "user_json"
        const val ORDER_TIME_ORDERED = "time_ordered"
        const val ORDER_USER_UID = "user_uid"
        const val ORDER_MENU_ITEM_UID = "menu_item_uid"
        const val ORDER_PRODUCT_HEADER = "product_header"
        const val ORDER_PRODUCT_CONTENT = "product_content"
        const val ORDER_QUANTITY = "quantity"
        const val ORDER_USER_OBSERVATION = "user_observation"
        const val ORDER_USER_CONFIRMED_DELIVERY = "user_confirmed_delivery"
        //endregion
    }

    object StorageNames {
        const val MENU_IMAGES = "menu_images"
    }
}