package com.github.caioreigot.girafadoces.data.model

sealed class ServiceResult {
    object Success : ServiceResult()
    class Error(val errorType: ErrorType) : ServiceResult()
}
