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
