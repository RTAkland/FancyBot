/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.Hitokoto
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class HitokotoCommand : BaseCommand() {
    override val commandNames = listOf("/hitokoto", "/1", "一言", "1")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val response = Http.get<Hitokoto>("https://v1.hitokoto.cn/?c=b")
        val msg = MessageChain.Builder()
            .addText("⌊${response.sentence}⌉   ---《${response.from}》")
            .addNewLine()
            .addText("https://hitokoto.cn/?uuid=${response.uuid}")
            .build()
        message.reply(msg)
    }
}