/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class RUACommand : BaseCommand() {
    override val commandNames = listOf("/rua", "rua")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("使用rua @xxxx")
                .addText("即可rua某人啦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        try {
            val target = message.message.find { it.type == ArrayMessageType.at }?.data!!
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addAt(target.qq.toString().toLong())
                .addNewLine()
                .addText(message.sender.nickname)
                .addText(" ")
                .addText("rua了你~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        } catch (_: NullPointerException) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("要@才有用哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}