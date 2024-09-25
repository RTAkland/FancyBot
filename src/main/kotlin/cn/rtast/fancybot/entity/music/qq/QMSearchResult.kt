/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/25
 */


package cn.rtast.fancybot.entity.music.qq

data class QMSearchResult(
    val id: Long,
    val name: String,
    val singers: String,
    val interval: Int,
    val mid: String,
    val timestamp: Long,
    val albumUrl: String
)