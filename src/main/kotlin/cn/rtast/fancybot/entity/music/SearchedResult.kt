/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.entity.music

data class SearchedResult(
    val id: Long,
    val name: String,
    val artists: String,
    val timestamp: Long,
)