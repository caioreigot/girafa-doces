package com.github.caioreigot.girafadoces.data.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Bitmap::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(header)
        parcel.writeString(content)
        parcel.writeParcelable(image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuItem> {
        override fun createFromParcel(parcel: Parcel): MenuItem {
            return MenuItem(parcel)
        }

        override fun newArray(size: Int): Array<MenuItem?> {
            return arrayOfNulls(size)
        }
    }
}
