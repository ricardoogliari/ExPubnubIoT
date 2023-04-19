package com.atitus.ex_pubnub

data class MessageFromPubnb(
    val sensor: String,
    val value: Int,
)