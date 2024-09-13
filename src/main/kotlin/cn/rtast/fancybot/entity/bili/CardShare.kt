/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.entity.bili

import com.google.gson.annotations.SerializedName

data class CardShare(
    val meta: Meta,
) {
    data class Meta(
        @SerializedName("detail_1")
        val detail: Detail?,
        val news: News?
    )

    data class Detail(
        val title: String,
        @SerializedName("qqdocurl")
        val qqDocUrl: String,
    )

    data class News(
        val tag: String,
        val jumpUrl: String
    )
}