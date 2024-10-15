/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("Echo")
class EchoCommand : BaseCommand() {
    override val commandNames = listOf("/echo")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (message.sender.userId !in configManager.admins) {
            message.reply("你不许用echo")
            return
        }
        listener.sendGroupMessage(message.groupId, args.joinToString(" "))
        insertActionRecord(CommandAction.Echo, message.sender.userId)
    }
}

@CommandDescription("精神错乱(×), 胡言乱语(√)")
class ShuffleEchoCommand : BaseCommand() {
    override val commandNames = listOf("/secho", "/se")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (message.sender.userId !in configManager.admins) {
            message.reply("你不许用echo")
            return
        }
        message.reply(message.rawMessage.toList().shuffled().joinToString(""))
        insertActionRecord(CommandAction.Echo, message.sender.userId)
    }
}