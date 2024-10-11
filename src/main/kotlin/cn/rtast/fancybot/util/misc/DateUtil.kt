/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util.misc

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zoneId = ZoneId.of("Asia/Shanghai")

fun Long.isSameDay(timestamp2: Long): Boolean {
    val date1 = Instant.ofEpochSecond(this).atZone(zoneId).toLocalDate()
    val date2 = Instant.ofEpochSecond(timestamp2).atZone(zoneId).toLocalDate()
    return date1 == date2
}

fun Long.convertToDate(): String {
    val instant = Instant.ofEpochSecond(this)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zoneId)
    return formatter.format(instant)
}
