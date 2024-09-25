/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.commands
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class HelpCommand : BaseCommand() {
    override val commandNames = listOf("/help", "/帮助")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val allCommands = commands.sortedBy { it::class.simpleName }.joinToString("\n") {
            "[${
                it.javaClass.name.split(".")
                    .last().replace("Command", "")
            }] 命令:${it.commandNames.joinToString(",")}"
        }
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addNewLine()
            .addText(allCommands)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}