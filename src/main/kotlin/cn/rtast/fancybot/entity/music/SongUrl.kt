/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/16
 */


package cn.rtast.fancybot.entity.music

data class SongUrl(
    val data: List<Data>,
) {
    data class Data(
        val url: String,
    )
}