/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/20
 */


package cn.rtast.fancybot.entity.wiki

import com.google.gson.annotations.SerializedName

data class PageInfoResponse(
    val query: Query,
) {
    data class Query(
        val pages: Map<String, PageInfo>,
    )

    data class PageInfo(
        @SerializedName("fullurl")
        val fullUrl: String,
    )
}