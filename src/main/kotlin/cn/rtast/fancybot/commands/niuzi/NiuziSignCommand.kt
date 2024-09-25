/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("牛子签到~")
class NiuziSignCommand : BaseCommand() {
    override val commandNames = listOf("牛子签到", "签到")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (niuziManager.isSigned(message.sender.userId)) {
            message.reply("你今天已经对你的牛子使用了签到啦,明天再来吧~")
            return
        }
        val afterStatus = niuziManager.sign(message.sender.userId)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("签到成功~")
            .addNewLine()
            .addText("你的牛子增加了${afterStatus.first}cm~ 总长度为: ${afterStatus.second?.length}")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}
