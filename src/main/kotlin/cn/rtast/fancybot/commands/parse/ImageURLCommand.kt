/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.configManager
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.asNode

object ImageURLCommand {

    fun getImageUrl(message: GetMessage.Message): List<String> {
        try {
            val images = mutableListOf<String>()
            message.message.forEach {
                if (it.type == ArrayMessageType.image) images.add(it.data.file!!)
                if (it.type == ArrayMessageType.mface) images.add(it.data.url!!)
            }
            return images
        } catch (_: NullPointerException) {
            return emptyList()
        }
    }

    suspend fun callback(groupMessage: GroupMessage, message: GetMessage.Message) {
        val images = getImageUrl(message)
        if (images.isEmpty()) {
            groupMessage.reply("这个消息里没有图片呢!")
        } else {
            if (images.size == 1) {
                val msg = MessageChain.Builder()
                    .addText(images.first().makeShortLink())
                    .addNewLine(2)
                    .addText(images.first())
                    .build()
                groupMessage.reply(msg)
            } else {
                val messages = mutableListOf<MessageChain>()
                images.forEach {
                    val msg = MessageChain.Builder()
                        .addText(it)
                        .addNewLine(2)
                        .build()
                    messages.add(msg)
                }
                groupMessage.reply(messages.asNode(configManager.selfId))
            }
        }
    }
}