/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/29
 */


package cn.rtast.fancybot.entity.setu

data class SetuV2(
    val pid: Long,
    val uid: Long,
    val title: String,
    val author: String,
    val urls: URLS,
    val r18: Boolean,
    val tags: List<String>
) {
    data class URLS(
        val regular: String,
    )
}