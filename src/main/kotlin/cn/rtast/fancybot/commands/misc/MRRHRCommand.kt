/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/21
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
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

@CommandDescription("没惹任何人")
class MRRHRCommand : BaseCommand() {
    override val commandNames = listOf("/mr")

    private val mrrhrImageBytes = Resources.loadFromResourcesAsBytes("misc/mrrhr.png")
    private val font = Font("SimSun", Font.BOLD, 60)

    private fun generateImage(content: String): String {
        val img = ImageIO.read(mrrhrImageBytes!!.inputStream())
        val g2d = img.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = Color.BLACK
        g2d.font = font
        g2d.drawCenteredText(content, 315, 466)
        g2d.dispose()
        val imgBase64 = img.toByteArray().encodeToBase64()
        return imgBase64
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val image = generateImage(content)
        message.reply(Image(image, true))
    }
}