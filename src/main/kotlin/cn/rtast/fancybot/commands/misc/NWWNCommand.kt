/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/15
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.misc.Resources
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.segment.Image
import cn.rtast.rob.util.BaseCommand
import java.awt.Color
import java.awt.Font
import javax.imageio.ImageIO

@CommandDescription("那我问你")
class NWWNCommand : BaseCommand() {
    override val commandNames = listOf("nwwn", "那我问你")
    private val originNWWNImage = Resources.loadFromResourcesAsBytes("misc/nwwn.png")
    private val font = Font("Serif", Font.ITALIC, 55)

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        try {
            val keyword = args.first()
            val img = ImageIO.read(originNWWNImage!!.inputStream())
            val g2d = img.createGraphics()
            g2d.color = Color.BLACK
            g2d.font = font
            g2d.drawString(keyword, 290, 70)
            g2d.dispose()
            val imgBase64 = img.toByteArray().encodeToBase64()
            val msg = Image(imgBase64, true)
            message.reply(msg)
        } catch (e: Exception) {
            message.reply("生成失败: ${e.message}")
        }
    }
}