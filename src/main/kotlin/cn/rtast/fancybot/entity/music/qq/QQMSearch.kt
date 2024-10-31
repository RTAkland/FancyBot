/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/25
 */


package cn.rtast.fancybot.entity.music.qq

import com.google.gson.annotations.SerializedName

data class QQMSearch(
    val response: Response,
) {
    data class Response(
        val data: Data,
    )

    data class Data(
        val song: Songs,
    )

    data class Songs(
        val list: List<Song>,
    )

    data class Song(
        @SerializedName("songid")
        val songId: Long,
    )
}