/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/14
 */


package cn.rtast.fancybot.entity.bili

data class ShortUrl(
    val data: Data,
) {
    data class Data(
        val content: String,
    )
}