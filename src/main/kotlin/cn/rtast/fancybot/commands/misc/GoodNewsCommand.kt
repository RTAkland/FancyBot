/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/20
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.util.misc.Resources
import cn.rtast.fancybot.util.misc.drawCenteredText
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.segment.Image
import cn.rtast.rob.util.BaseCommand
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import javax.imageio.ImageIO

class GoodNewsCommand: BaseCommand() {
    override val commandNames = listOf("xb", "喜报")

    private val goodNewsImageBytes = Resources.loadFromResourcesAsBytes("misc/good_news.png")
    private val font = Font("SimSun", Font.BOLD, 130)

    private fun generateImage(content: String): String {
        val img = ImageIO.read(goodNewsImageBytes!!.inputStream())
        val g2d = img.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = Color.RED
        g2d.font = font
        g2d.drawCenteredText(content, 650, 500)
        g2d.dispose()
        val imgBase64 = img.toByteArray().encodeToBase64()
        return imgBase64
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val imgBase64 = generateImage(content)
        message.reply(Image(imgBase64, true))
    }
}