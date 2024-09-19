/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

class EchoCommand : BaseCommand() {
    override val commandNames = listOf("/echo")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        listener.sendGroupMessage(message.groupId, args.joinToString(" "))
    }
}