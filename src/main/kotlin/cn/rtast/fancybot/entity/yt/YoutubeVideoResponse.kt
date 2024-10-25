/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/15
 */


package cn.rtast.fancybot.entity.yt


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
        val standard: Standard
    )

    data class Standard(
        val url: String,
    )

    data class Statistics(
        val viewCount: String,
        val likeCount: String,
        val favoriteCount: String,
        val commentCount: String,
    )
}