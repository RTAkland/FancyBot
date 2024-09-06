/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import java.net.InetAddress

class NslookupCommand : BaseCommand() {
    override val commandNames = listOf("/nslookup", "/ns")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("域名: ${args.first()}解析后的IP地址为: ")
            .addText(InetAddress.getByName(args.first()).hostAddress)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}