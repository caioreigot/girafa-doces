package com.github.caioreigot.girafadoces.data

import android.content.Context
import androidx.annotation.StringRes

class ResourcesProvider(context: Context) {

    private var mContext: Context = context

    fun getString(@StringRes stringID: Int): String {
        return mContext.getString(stringID)
    }
}