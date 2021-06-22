package com.github.caioreigot.girafadoces.data.helper

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourcesProvider @Inject constructor(@ApplicationContext private val context: Context) {

    fun getString(@StringRes stringID: Int): String = context.getString(stringID)
}