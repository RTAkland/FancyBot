/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.record

import cn.rtast.fancybot.signManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage


class SignCommand : BaseCommand() {
    override val commandNames = listOf("/签到", "/sign")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (signManager.isSigned(message.sender.userId)) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你今天已经签到过啦, 明天再来吧~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val randomPoint = signManager.sign(message.sender.userId)
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("你获得了 $randomPoint 点分数!")
            .addNewLine()
            .addText("发送‘/我的点数’ 或者 ‘/mp’ 即可查看当前的点数")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}
