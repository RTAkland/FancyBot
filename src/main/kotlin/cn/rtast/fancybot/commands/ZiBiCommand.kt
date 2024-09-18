/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/18
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class ZiBiCommand : BaseCommand() {
    override val commandNames = listOf("/自闭", "自闭")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        try {
            val duration = if (args.isEmpty()) 1 else args.first().toInt()
            message.sender.ban(duration * 60)
            val msg = MessageChain.Builder()
                .addText("你先自闭${duration}分钟吧~")
                .build()
            message.reply(msg)
        } catch (_: Exception) {
            val msg = MessageChain.Builder()
                .addText("你输入有误所以我决定让你自闭1天~")
                .build()
            message.reply(msg)
            message.sender.ban(86400)
        }
    }
}