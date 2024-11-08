/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/1
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.onebot.OneBotAction
import cn.rtast.rob.util.BaseCommand

@CommandDescription("让你的消息得到回应(reaction)!")
class ReactionCommand : BaseCommand() {
    override val commandNames = listOf("reaction", "回应")

    companion object {
        suspend fun reaction(action: OneBotAction, groupId: Long, messageId: Long, limit: Int = 30) {
            (0..limit - 1).forEach { action.reaction(groupId, messageId, QQFace.entries.random().id.toString()) }
        }
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) =
        reaction(message.action, message.groupId, message.messageId)
}