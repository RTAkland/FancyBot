/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.entity.jrrp

data class JrrpRecord(
    val id: Long,
    val timestamp: Long,
    var points: Long,
)