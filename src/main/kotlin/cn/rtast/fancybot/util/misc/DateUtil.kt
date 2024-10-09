/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util.misc

import java.time.Instant
import java.time.ZoneId

fun Long.isSameDay(timestamp2: Long): Boolean {
    val zoneId = ZoneId.of("Asia/Shanghai")
    val date1 = Instant.ofEpochSecond(this).atZone(zoneId).toLocalDate()
    val date2 = Instant.ofEpochSecond(timestamp2).atZone(zoneId).toLocalDate()
    return date1 == date2
}
