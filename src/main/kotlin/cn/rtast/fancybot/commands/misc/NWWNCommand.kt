/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/15
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

@CommandDescription("那我问你")
class NWWNCommand : BaseCommand() {
    override val commandNames = listOf("nwwn", "那我问你")
    private val originNWWNImage = Resources.loadFromResourcesAsBytes("misc/nwwn.png")
    private val font = Font("SimSun", Font.BOLD, 55)

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        try {
            val keyword = args.joinToString(" ")
            val img = ImageIO.read(originNWWNImage!!.inputStream())
            val g2d = img.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
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

@CommandDescription("太几把搞笑了")
class TJBGXLCommand: BaseCommand() {
    override val commandNames = listOf("太几把搞笑了", "tjbgxl", "jb")

    private val originImage = Resources.loadFromResourcesAsBytes("misc/tjbgxl.png")
    private val font = Font("SimSun", Font.BOLD, 45)

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        try {
            val keyword = args.joinToString(" ")
            val img = ImageIO.read(originImage!!.inputStream())
            val g2d = img.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.color = Color.BLACK
            g2d.font = font
            g2d.drawCenteredText(keyword, 380, 620)
            g2d.dispose()
            val imgBase64 = img.toByteArray().encodeToBase64()
            val msg = Image(imgBase64, true)
            message.reply(msg)
        } catch (e: Exception) {
            message.reply("生成失败: ${e.message}")
        }
    }
}

@CommandDescription("香奈美语句生成")
class XNMCommand: BaseCommand() {
    override val commandNames = listOf("xnm", "香奈美")

    private val font = Font("SimSun", Font.BOLD, 55)
    private val originImage = Resources.loadFromResourcesAsBytes("misc/xnm.png")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        try {
            val keyword = args.first()
            val keyword2 = args.last()
            val img = ImageIO.read(originImage!!.inputStream())
            val g2d = img.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.color = Color.BLACK
            g2d.font = font
            g2d.drawCenteredText(keyword, 510, 55)
            g2d.drawCenteredText(keyword2, 510, 140)
            g2d.dispose()
            val imgBase64 = img.toByteArray().encodeToBase64()
            val msg = Image(imgBase64, true)
            message.reply(msg)
        } catch (e: Exception) {
            message.reply("生成失败: ${e.message}")
        }
    }
}

class SJCommand: BaseCommand() {
    override val commandNames = listOf("sj")

    private val font = Font("SimSun", Font.BOLD, 20)
    private val originImage = Resources.loadFromResourcesAsBytes("misc/sj.png")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        try {
            val keyword = args.joinToString(" ")
            val img = ImageIO.read(originImage!!.inputStream())
            val g2d = img.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.color = Color.BLACK
            g2d.font = font
            g2d.drawCenteredText(keyword, 126, 230)
            g2d.dispose()
            val imgBase64 = img.toByteArray().encodeToBase64()
            val msg = Image(imgBase64, true)
            message.reply(msg)
        } catch (e: Exception) {
            message.reply("生成失败: ${e.message}")
        }
    }
}