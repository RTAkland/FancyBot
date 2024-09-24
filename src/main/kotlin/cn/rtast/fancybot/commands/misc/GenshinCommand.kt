/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

class GenshinCommand : BaseCommand() {
    override val commandNames = listOf("元神", "原神")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        message.reply("你原神牛魔呢")
    }
}