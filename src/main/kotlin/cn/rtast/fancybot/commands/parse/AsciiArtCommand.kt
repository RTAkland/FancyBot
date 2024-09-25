/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/20
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class AsciiArtCommand : BaseCommand() {
    override val commandNames = listOf("/ascii", "/asc")

    companion object {
        private const val FONT_SIZE = 12
        private val font = Font("Monospaced", Font.PLAIN, FONT_SIZE)
        val waitingList = mutableListOf<Long>()

        private fun BufferedImage.convertToAscii(): String {
            val width = this.width
            val height = this.height
            val asciiArt = StringBuilder()
            for (y in 0 until height step 2) {
                for (x in 0 until width) {
                    val pixel = Color(this.getRGB(x, y))
                    val luminance = (0.2126 * pixel.red + 0.7152 * pixel.green + 0.0722 * pixel.blue).roundToInt()
                    val asciiChar = when (luminance) {
                        in 0..15 -> ' '
                        in 16..31 -> '.'
                        in 32..47 -> ':'
                        in 48..63 -> '-'
                        in 64..79 -> '='
                        in 80..95 -> '+'
                        in 96..111 -> '*'
                        in 112..127 -> '#'
                        in 128..143 -> '%'
                        in 144..159 -> '@'
                        in 160..175 -> 'a'
                        in 176..191 -> 'o'
                        in 192..207 -> 'e'
                        in 208..223 -> 'h'
                        in 224..239 -> 'u'
                        else -> '$'
                    }
                    asciiArt.append(asciiChar)
                }
                asciiArt.append("\n")
            }
            return asciiArt.toString()
        }


        private fun String.saveAsciiArtToImage(width: Int, height: Int): String {
            val imgWidth = width * FONT_SIZE / 2
            val imgHeight = height * FONT_SIZE / 2
            val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
            val g2d = img.createGraphics()
            g2d.color = Color.BLACK
            g2d.fillRect(0, 0, imgWidth, imgHeight)
            g2d.color = Color.WHITE
            g2d.font = font
            val lines = this.split("\n")
            for ((index, line) in lines.withIndex()) {
                g2d.drawString(line, 0, (index + 1) * FONT_SIZE)
            }
            g2d.dispose()
            return img.toByteArray().encodeToBase64()
        }

        suspend fun callback(message: GroupMessage) {
            if (message.sender.userId in waitingList) {
                if (message.message.any { it.type == ArrayMessageType.image }) {
                    val url = message.message.find { it.type == ArrayMessageType.image }!!.data.file!!
                    val bufferedImage = withContext(Dispatchers.IO) {
                        ImageIO.read(URI(url).toURL())
                    }
                    val imageBase64 =
                        bufferedImage.convertToAscii().saveAsciiArtToImage(bufferedImage.width, bufferedImage.height)
                    val msg = MessageChain.Builder().addImage(imageBase64, true).build()
                    message.reply(msg)
                } else {
                    message.reply("回复错误已取消本次操作")
                }
                waitingList.removeIf { it == message.sender.userId }
            }
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (message.sender.userId !in waitingList) {
            message.reply("请继续发送一张图片，如果输入错误则取消本次操作")
            waitingList.add(message.sender.userId)
            return
        } else {
            message.reply("先发送`/ascii`再继续操作吧~")
            return
        }
    }
}