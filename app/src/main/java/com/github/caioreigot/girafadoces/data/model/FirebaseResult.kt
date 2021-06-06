package com.github.caioreigot.girafadoces.data.model

sealed class FirebaseResult {
    object Success : FirebaseResult()
    class Error(val errorType: ErrorType) : FirebaseResult()
}
