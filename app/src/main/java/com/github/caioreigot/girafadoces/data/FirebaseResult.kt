package com.github.caioreigot.girafadoces.data

sealed class FirebaseResult {
    class Success(val message: String) : FirebaseResult()
    class Error(val message: String) : FirebaseResult()
}
