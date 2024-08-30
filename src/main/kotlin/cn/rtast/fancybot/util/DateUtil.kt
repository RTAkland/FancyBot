/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.isSameDay(other: Long): Boolean {
    val currentDate = LocalDate.now()
    val dateToCompare = Instant.ofEpochSecond(other)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return currentDate == dateToCompare
}
