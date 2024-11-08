/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/13
 */


package cn.rtast.fancybot.commands.reply

import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.commands.parse.ImageURLCommand
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.util.file.getFileType
import cn.rtast.fancybot.util.misc.ImageBed
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.onebot.toNode
import okio.IOException

object ImageBedCommand {
    suspend fun execute(getMsg: GetMessage.Message, message: GroupMessage) {
        val imagesUrl = ImageURLCommand.getImageUrl(getMsg)
        if (imagesUrl.isEmpty()) {
            message.reply("这个消息里没有图片呢!")
        } else {
            try {
                if (imagesUrl.size == 1) {
                    val imageByteArray = imagesUrl.first().toURL().readBytes()
                    val imageFileType = imageByteArray.getFileType()
                    val imageBedUrl = ImageBed.upload(imageByteArray, imageFileType)
                    val msg = MessageChain.Builder()
                        .addText(imageBedUrl.makeShortLink())
                        .addNewLine(2)
                        .addText(imageBedUrl)
                        .build()
                    message.reply(msg)
                } else {
                    val messages = mutableListOf<MessageChain>()
                    imagesUrl.forEach {
                        val imageByteArray = it.toURL().readBytes()
                        val imageFileType = imageByteArray.getFileType()
                        val imageBedUrl = ImageBed.upload(imageByteArray, imageFileType)
                        val msg = MessageChain.Builder()
                            .addText(imageBedUrl.makeShortLink())
                            .addNewLine(2)
                            .addText(imageBedUrl)
                            .build()
                        messages.add(msg)
                    }
                    message.reply(messages.toNode(configManager.selfId))
                }
            } catch (_: IOException) {
                message.reply("上传失败: 图片已过期")
            } catch (e: Exception) {
                e.printStackTrace()
                message.reply("上传失败: ${e.message}")
            }
        }
    }
}