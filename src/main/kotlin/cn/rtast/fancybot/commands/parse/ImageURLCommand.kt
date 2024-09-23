/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain

object ImageURLCommand {
    suspend fun callback(groupMessage: GroupMessage, message: GetMessage.Data) {
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("哈哈哈哈图片来咯~")
            .addNewLine()
        val image = message.message.find { it.type == ArrayMessageType.image }
        if (image == null) {
            val gif = message.message.find { it.type == ArrayMessageType.mface }!!
            msg.addText(gif.data.url!!)
        } else {
            msg.addText(image.data.file!!)
        }
        groupMessage.reply(msg.build())
    }
}