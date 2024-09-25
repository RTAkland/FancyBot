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
        val data: Data
    )

    data class Data(
        val song: Songs
    )

    data class Songs(
        val list: List<Song>
    )

    data class Song(
        @SerializedName("albummid")
        val albumMid: String,
        val interval: Int,
        @SerializedName("songmid")
        val songMid: String,
        @SerializedName("songid")
        val songId: Long,
        @SerializedName("songname")
        val songName: String,
        val singer: List<Singer>,
    )

    data class Singer(
        val name: String
    )
}