/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.makeGif
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import com.madgag.gif.fmsware.GifDecoder
import java.net.URI

@CommandDescription("倒放GIF")
class ReverseGIFCommand : BaseCommand() {
    override val commandNames = listOf("倒放")

    companion object {
        private val waitingList = mutableListOf<Long>()

        private fun reverseGif(gifUrl: String): String {
            val gifStream = URI(gifUrl).toURL().openStream()
            val decoder = GifDecoder()
            decoder.read(gifStream)
            val frames = (0 until decoder.frameCount).map { decoder.getFrame(it) }.reversed()
            val gifBytes = decoder.makeGif(frames)
            return gifBytes.encodeToBase64()
        }

        suspend fun callback(message: GroupMessage) {
            if (message.sender.userId !in waitingList) return
            waitingList.removeIf { it == message.sender.userId }
            val gifUrl = if (message.message.find { it.type == ArrayMessageType.mface } == null) {
                message.message.find { it.type == ArrayMessageType.image }!!.data.file!!
            } else {
                message.message.find { it.type == ArrayMessageType.mface }!!.data.url!!
            }
            val base64String = this.reverseGif(gifUrl)
            val msg = MessageChain.Builder().addImage(base64String, true).build()
            message.reply(msg)
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (message.sender.userId !in waitingList) {
            message.reply("发送一张动图来继续操作")
            waitingList.add(message.sender.userId)
        } else {
            message.reply("发送错误本次操作已取消")
            waitingList.removeIf { it == message.sender.userId }
        }
    }
}