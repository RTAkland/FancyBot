/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

object ImageURLCommand {

    suspend fun callback(listener: OBMessage, message: GetMessage) {
        val msg = MessageChain.Builder()
            .addReply(message.data.messageId)
            .addText("哈哈哈哈图片来咯~")
            .addNewLine()
        val image = message.data.message.find { it.type == ArrayMessageType.image }
        if (image == null) {
            val gif = message.data.message.find { it.type == ArrayMessageType.mface }!!
            msg.addText(gif.data.url!!)
        } else {
            msg.addText(image.data.file!!)

        }
        listener.sendGroupMessage(message.data.groupId!!, msg.build())
    }
}