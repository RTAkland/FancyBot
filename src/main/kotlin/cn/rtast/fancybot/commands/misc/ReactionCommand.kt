/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/1
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("让你的消息得到回应(reaction)!")
class ReactionCommand : BaseCommand() {
    override val commandNames = listOf("reaction", "回应")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        (0..29).forEach { _ ->
            message.reaction(QQFace.entries.random())
        }
    }
}