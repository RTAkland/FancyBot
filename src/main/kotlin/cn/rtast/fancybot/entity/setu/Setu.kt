/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.entity.setu

import com.google.gson.annotations.SerializedName

data class Setu(
    val urls: URLS,
    val r18: Boolean,
    @SerializedName("author_id")
    val authorId: Long,
) {
    data class URLS(
        val large: String,
    )
}