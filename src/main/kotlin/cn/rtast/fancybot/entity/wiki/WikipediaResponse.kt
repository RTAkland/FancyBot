/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/19
 */


package cn.rtast.fancybot.entity.wiki

data class WikipediaResponse(
    val query: Query
) {
    data class Query(
        val search: List<Search>
    )

    data class Search(
        val title: String,
        val snippet: String,
    )
}