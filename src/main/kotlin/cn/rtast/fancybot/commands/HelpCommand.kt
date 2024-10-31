/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands
import cn.rtast.fancybot.configManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@CommandDescription("帮助")
class HelpCommand : BaseCommand() {
    override val commandNames = listOf("/help", "/帮助")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val node = NodeMessageChain.Builder()
            val msg = MessageChain.Builder()
            commands.sortedBy { it::class.simpleName }.toMutableList().forEach {
                val description = if (!it::class.hasAnnotation<CommandDescription>()) "暂无描述"
                else it::class.findAnnotation<CommandDescription>()?.description!!
                val commandName = it::class.simpleName?.replace("Command", "")!!
                val commandNames = it.commandNames.joinToString(",")
                msg.addText("[$commandName] [$description] 命令: $commandNames").addNewLine()
            }
            msg.addText("共计${commands.size}条命令")
            node.addMessageChain(
                MessageChain.Builder().addText("你可以使用`/help <指令>`来获取这个指令的帮助信息").build(),
                configManager.selfId
            )
            node.addMessageChain(msg.build(), configManager.selfId)
            message.reply(node.build())
        } else {
            val commandName = args.first().replace("/", "")
            val matchedCommand = commands.find {
                it.commandNames.map { map -> map.replace("/", "").lowercase() }.contains(commandName)
            }
            if (matchedCommand == null) {
                message.reply("未查询到该命令")
                return
            }
            val description = matchedCommand::class.findAnnotation<CommandDescription>()?.description ?: "暂无描述"
            val msg = MessageChain.Builder()
                .addText("类名: ${matchedCommand::class.simpleName}")
                .addNewLine()
                .addText("指令: ${matchedCommand.commandNames.joinToString(",")}")
                .addNewLine()
                .addText("描述: $description")
                .build()
            message.reply(msg)
        }
    }
}