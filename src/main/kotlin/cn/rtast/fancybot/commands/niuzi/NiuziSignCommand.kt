/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.misc.getUserName
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.util.BaseCommand

@CommandDescription("牛子签到~")
class NiuziSignCommand : BaseCommand() {
    override val commandNames = listOf("牛子签到", "签到")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (niuziManager.isSigned(message.sender.userId)) {
            message.reply("你今天已经对你的牛子使用了签到啦,明天再来吧~")
            return
        }
        val username = message.action.getUserName(message.groupId, message.sender.userId)
        val afterStatus = niuziManager.sign(message.sender.userId, username)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("签到成功~")
            .addNewLine()
            .addText("你的牛子增加了${afterStatus.first}cm~ 总长度为: ${afterStatus.second?.length}")
            .build()
        message.reply(msg)
    }
}
