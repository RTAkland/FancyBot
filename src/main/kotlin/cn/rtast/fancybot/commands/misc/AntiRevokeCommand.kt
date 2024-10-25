/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/1
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.ArrayMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("防撤回(获取消息)")
class AntiRevokeCommand : BaseCommand() {
    override val commandNames = listOf("/revoke", "/rv")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (message.sender.isAdmin || message.sender.isOwner) {
            val messageId = args.first().toLong()
            val getMsg = message.action.getMessage(messageId)
            val msgList = mutableListOf<ArrayMessage>()
            msgList.add(ArrayMessage(ArrayMessageType.at, ArrayMessage.Data(qq = message.sender.userId.toString())))
            msgList.add(ArrayMessage(ArrayMessageType.text, ArrayMessage.Data(text = "\n被撤回的消息如下: \n")))
            msgList.addAll(getMsg.message)
            message.action.sendGroupMessage(message.groupId, msgList)
            insertActionRecord(CommandAction.AntiRevoke, message.sender.userId, getMsg.messageId.toString())
        } else {
            message.reply("你不许用防撤回")
        }
    }
}