/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class LikeMeCommand : BaseCommand() {
    override val commandNames = listOf("赞我")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        listener.sendLike(message.sender.userId, 10)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("👍了你十下~~😁")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}