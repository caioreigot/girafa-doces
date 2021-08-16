package com.github.caioreigot.girafadoces.data.model

data class Order(
    var userUid: String = "",
    var user: User,
    val product: Product,
    val timeOrdered: String = "",
    val quantity: String = "",
    val userObservation: String = ""
)