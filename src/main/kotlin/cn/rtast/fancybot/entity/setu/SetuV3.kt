/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/6
 */


package cn.rtast.fancybot.entity.setu

data class SetuV3(
    val pid: Long,
    val uid: Long,
    val title: String,
    val author: String,
    val url: String,
    val r18: Boolean,
)