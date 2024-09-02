/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/1
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.util.toJson
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class AntiRevokeCommand : BaseCommand() {
    override val commandNames = listOf("/revoke", "/rv", "/防撤回")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val messageId = args.first().toLong()
        listener.getMessage(messageId, "revoke", message.groupId)
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("正在获取消息中...")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }

    companion object {
        suspend fun getMessageCallback(listener: OBMessage, message: GetMessage) {
            val msg = MessageChain.Builder()
                .addAt(message.data.sender.userId)
                .addNewLine()
                .addText("被撤回的消息如下: ")
                .addNewLine()
                .addText(message.data.message.toJson())
                .build()
            listener.sendGroupMessage(message.data.groupId!!, msg)
        }
    }
}