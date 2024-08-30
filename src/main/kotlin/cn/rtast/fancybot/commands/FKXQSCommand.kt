/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.fkxqs.FKXQS
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

class FKXQSCommand: BaseCommand() {
    override val commandNames = listOf("/fkxqs", "/fk", "/疯狂星期四")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val response = Http.get<FKXQS>("https://api.shadiao.pro/kfc").data.text
        listener.sendGroupMessage(message.groupId, response)
    }
}