/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@CommandDescription("帮助")
class HelpCommand : BaseCommand() {
    override val commandNames = listOf("/help", "/帮助")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val node = NodeMessageChain.Builder()
        val msg = MessageChain.Builder()
        commands.sortedBy { it::class.simpleName }.forEach {
            val description = if (!it::class.hasAnnotation<CommandDescription>()) "暂无描述"
            else it::class.findAnnotation<CommandDescription>()?.description!!
            val commandName = it::class.simpleName?.replace("Command", "")!!
            val commandNames = it.commandNames.joinToString(",")
            msg.addText("[$commandName] [$description] 命令: $commandNames")
                .addNewLine()
        }
        msg.addText("共计${commands.size}条命令")
        node.addMessageChain(msg.build(), message.sender.userId)
        message.reply(node.build())
    }
}