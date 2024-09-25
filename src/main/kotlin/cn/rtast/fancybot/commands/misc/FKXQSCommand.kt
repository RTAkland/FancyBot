/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.FKXQS
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("今天是疯狂星期四!")
class FKXQSCommand : BaseCommand() {
    override val commandNames = listOf("/fkxqs", "/fk", "/疯狂星期四")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val response = Http.get<FKXQS>("https://api.shadiao.pro/kfc").data.text
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText(response)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}