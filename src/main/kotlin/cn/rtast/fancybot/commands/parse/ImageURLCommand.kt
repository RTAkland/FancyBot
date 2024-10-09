/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType

object ImageURLCommand {

    fun getImageUrl(message: GetMessage.Data): String? {
        try {
            val image = message.message.find { it.type == ArrayMessageType.image }
            val url = if (image == null) {
                val gif = message.message.find { it.type == ArrayMessageType.mface }!!
                gif.data.url!!
            } else {
                image.data.file!!
            }
            return url
        } catch (_: NullPointerException) {
            return null
        }
    }

    suspend fun callback(groupMessage: GroupMessage, message: GetMessage.Data) {
        val url = getImageUrl(message)
        if (url == null) {
            groupMessage.reply("这个消息里没有图片呢!")
        } else {
            groupMessage.reply(url)
        }
    }
}