package com.github.caioreigot.girafadoces.data.model

enum class ErrorType {
    UNEXPECTED_ERROR,
    SERVER_ERROR,
    NOT_FOUND,
    EMPTY_FIELD,
    INVALID_EMAIL,
    INVALID_PHONE,
    EMAIL_ALREADY_REGISTERED,
    WEAK_PASSWORD,
    PASSWORD_CONFIRM_DONT_MATCH
}