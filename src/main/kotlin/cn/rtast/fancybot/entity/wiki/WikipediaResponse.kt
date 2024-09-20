/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/19
 */


package cn.rtast.fancybot.entity.wiki

import com.google.gson.annotations.SerializedName

data class WikipediaResponse(
    val query: Query
) {
    data class Query(
        val search: List<Search>
    )

    data class Search(
        @SerializedName("pageid")
        val pageId: Long,
        val title: String,
        val snippet: String,
    )
}