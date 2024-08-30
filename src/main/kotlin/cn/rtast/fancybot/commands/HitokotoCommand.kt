/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

class HitokotoCommand : BaseCommand() {
    override val commandNames = listOf("/hitokoto", "/1")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val response = Http.get("https://v1.jinrishici.com/rensheng/mengxiang.txt")
        listener.sendGroupMessage(message.groupId, response)
    }
}