package com.github.caioreigot.girafadoces.data

import com.github.caioreigot.girafadoces.data.model.ErrorType

sealed class FirebaseResult {
    object Success : FirebaseResult()
    class Error(val errorType: ErrorType) : FirebaseResult()
}
