/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.entity

data class Setu(
    val urls: URLS
) {
    data class URLS(
        val large: String
    )
}