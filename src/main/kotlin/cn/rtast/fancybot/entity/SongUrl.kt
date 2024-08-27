/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.entity

data class SongUrl(
    val data: List<Data>
) {
    data class Data(
        val url: String,
    )
}