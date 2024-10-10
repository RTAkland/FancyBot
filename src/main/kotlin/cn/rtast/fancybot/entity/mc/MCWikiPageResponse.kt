/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.entity.mc

import com.google.gson.annotations.SerializedName

data class MCWikiPageResponse(
    @SerializedName("redirect_target")
    val redirectTarget: String?,
    val httpCode: Int?,
    val id: Int,
    val title: String,
    val latest: Latest,
    val source: String,
    val license: License
) {
    data class Latest(
        val id: Int,
        val timestamp: String,
    )

    data class License(
        val url: String,
        val title: String,
    )
}