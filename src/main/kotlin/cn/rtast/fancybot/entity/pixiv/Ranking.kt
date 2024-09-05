/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.entity.pixiv

data class Ranking(
    val contents: List<Content>
) {
    data class Content(
        val title: String,
        val tags: List<String>,
        val url: String
    ) {
        fun getOriginUrl(): String {
            return url.replace("https://i.pximg.net", "https://proxy.rtast.cn/https/pixiv.microyu.workers.dev")
                .replace("/c/240x480/img-master/", "/img-original/")
                .replace("_master1200", "")
        }
    }
}