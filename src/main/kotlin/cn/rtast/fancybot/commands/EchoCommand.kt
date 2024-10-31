/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand

@CommandDescription("Echo")
class EchoCommand : BaseCommand() {
    override val commandNames = listOf("/echo")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (message.sender.isAdmin || message.sender.isOwner) {
            message.reply(args.joinToString(" "))
            insertActionRecord(CommandAction.Echo, message.sender.userId)
        } else {
            message.reply("你不许用echo")
        }
    }
}

@CommandDescription("精神错乱(×), 胡言乱语(√)")
class ShuffleEchoCommand : BaseCommand() {
    override val commandNames = listOf("/secho", "/se")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (message.sender.isAdmin || message.sender.isOwner) {
            message.reply(message.rawMessage.toList().shuffled().joinToString(""))
            insertActionRecord(CommandAction.Echo, message.sender.userId)
        } else {
            message.reply("你不许用echo")
        }
    }
}