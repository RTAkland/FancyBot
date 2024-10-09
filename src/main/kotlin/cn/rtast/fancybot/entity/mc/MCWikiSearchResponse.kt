/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/9
 */


package cn.rtast.fancybot.entity.mc

data class MCWikiSearchResponse(
    val pages: List<Page>
) {
    data class Page(
        val id: Int,
        val title: String,
        val thumbnail: Thumbnail?
    )

    data class Thumbnail(
        val url: String
    )
}