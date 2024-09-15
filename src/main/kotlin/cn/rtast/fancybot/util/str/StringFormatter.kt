/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.util.str

import java.awt.Graphics2D

fun setTruncat(origin: String, g2d: Graphics2D, maxWidth: Int = 500): String {
    val fontMetrics = g2d.fontMetrics
    val textWidth = fontMetrics.stringWidth(origin)
    return if (textWidth > maxWidth) {
        val ellipsisWidth = fontMetrics.stringWidth("...")
        val width = maxWidth - ellipsisWidth
        var endIndex = origin.length
        while (fontMetrics.stringWidth(origin.substring(0, endIndex)) > width && endIndex > 0) {
            endIndex--
        }
        origin.substring(0, endIndex) + "..."
    } else origin
}

fun Int.formatNumber(): String {
    return if (this >= 10000) {
        val result = this / 10000.0
        String.format("%.1f万", result)
    } else {
        this.toString()
    }
}

fun Int.formatNumberEnglish(): String {
    return when {
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 10_000 -> String.format("%.1fk", this / 1_000.0)
        this >= 1_000 -> String.format("%dk", this / 1_000)
        else -> this.toString()
    }
}
