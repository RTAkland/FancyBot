/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/29
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.util.BaseCommand

@Deprecated("已迁移到兑换列表中")
@CommandDescription("获得十张色图!")
class TenSetuCommand : BaseCommand() {
    override val commandNames = listOf("十张色图")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val msg = MessageChain.Builder()
            .addText("指令已经迁移到兑换列表中")
            .addNewLine()
            .addText("使用`/rdm 十张色图`来兑换吧~")
            .addNewLine()
            .addText("(需要20cm的牛子长度来兑换)")
            .addNewLine()
            .addText("此指令已废弃")
            .build()
        message.reply(msg)
    }
}