/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util

import java.time.Instant
import java.time.ZoneId

fun Long.isSameDay(otherTimestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val thisDate = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val otherDate = Instant.ofEpochMilli(otherTimestamp).atZone(zoneId).toLocalDate()
    return thisDate == otherDate
}
