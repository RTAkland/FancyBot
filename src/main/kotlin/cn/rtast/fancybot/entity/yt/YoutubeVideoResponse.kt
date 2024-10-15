/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/15
 */


package cn.rtast.fancybot.entity.yt

import com.google.gson.annotations.SerializedName


data class YoutubeVideoResponse(
    val items: List<Item>
) {
    data class Item(
        val snippet: Snippet,
        val statistics: Statistics
    )

    data class Snippet(
        val title: String,
        val thumbnails: Thumbnails,
        val channelTitle: String,
    )

    data class Thumbnails(
        @SerializedName("maxres")
        val maxRes: MaxRes
    )

    data class MaxRes(
        val url: String,
    )

    data class Statistics(
        val viewCount: String,
        val likeCount: String,
        val favoriteCount: String,
        val commentCount: String,
    )
}