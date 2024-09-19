/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/16
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class AboutCommand : BaseCommand() {
    override val commandNames = listOf("/about", "/关于")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addNewLine()
            .addText("Bot名称: FancyBot")
            .addNewLine()
            .addText("作者: RTAkland")
            .addNewLine()
            .addText("联系方式: me@rtast.cn")
            .addNewLine()
            .addText("项目地址: https://repo.rtast.cn/RTAkland/FancyBot")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}