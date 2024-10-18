/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/18
 */


package cn.rtast.fancybot.util.str

import kotlin.random.Random

fun generateRandomString(): String {
    val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val nameLength = Random.nextInt(3, 17)
    return (1..nameLength)
        .map { allowedChars.random() }
        .joinToString("")
}