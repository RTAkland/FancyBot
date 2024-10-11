/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

class TodayEatCommand : BaseCommand() {
    override val commandNames = listOf("今天吃什么")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        message.reply("你吃牛魔, 别吃了")
    }
}

class TodayDrinkCommand : BaseCommand() {
    override val commandNames = listOf("今天喝什么")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        message.reply("你喝牛魔呢")
    }
}