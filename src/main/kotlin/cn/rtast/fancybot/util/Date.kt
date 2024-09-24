/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.parseTimeStamp(): LocalDateTime {
    val instant = Instant.ofEpochMilli(this)
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}