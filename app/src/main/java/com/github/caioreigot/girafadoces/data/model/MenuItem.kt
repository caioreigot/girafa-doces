package com.github.caioreigot.girafadoces.data.model

import android.graphics.Bitmap
import com.google.firebase.database.PropertyName

data class MenuItem(
    var uid: String = "",

    @get:PropertyName(Global.DatabaseNames.MENU_ITEM_HEADER)
    @set:PropertyName(Global.DatabaseNames.MENU_ITEM_HEADER)
    var header: String = "",

    @get:PropertyName(Global.DatabaseNames.MENU_ITEM_CONTENT)
    @set:PropertyName(Global.DatabaseNames.MENU_ITEM_CONTENT)
    var content: String = "",

    var image: Bitmap? = null
)
