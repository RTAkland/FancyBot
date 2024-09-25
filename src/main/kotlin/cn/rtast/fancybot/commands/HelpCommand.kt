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
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("AAAA")
class HelpCommand : BaseCommand() {
    override val commandNames = listOf("/help", "/帮助")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val msg = MessageChain.Builder()
        commands.sortedBy { it::class.simpleName }.forEach {
            val description =
                if (!it.javaClass.isAnnotationPresent(CommandDescription::class.java)) {
                    "暂无描述"
                } else {
                    it.javaClass.getAnnotation(CommandDescription::class.java)?.description!!
                }
            val commandName = it::class.simpleName?.replace("Command", "")!!
            val commandNames = it.commandNames.joinToString(",")
            msg.addText("[$commandName] [$description] 命令: $commandNames")
                .addNewLine()
        }
        message.reply(msg.build())
    }
}