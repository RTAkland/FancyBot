/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.entity.pixiv

import com.google.gson.annotations.SerializedName

data class Ranking(
    val contents: List<Content>
) {
    data class Content(
        val title: String,
        val tags: List<String>,
        val url: String,
        @SerializedName("user_name")
        val userName: String,
        @SerializedName("user_id")
        val userId: Long,
        @SerializedName("illust_id")
        val illustId: Long,
        val date: String,
    ) {
        fun getOriginUrl(): String {
            return url.replace("https://i.pximg.net", "https://i.pixiv.re")
                .replace("/c/240x480/img-master/", "/img-original/")
                .replace("_master1200", "")
        }
    }
}