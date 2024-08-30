/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.util.file.SignManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

private val signManager = SignManager()

class SignCommand : BaseCommand() {
    override val commandName = "/签到"

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (signManager.isSigned(message.sender.userId)) {
            listener.sendGroupMessage(message.groupId, "你今天已经签到, 明天再来吧~")
            return
        }
        val randomPoint = signManager.sign(message.sender.userId)
        listener.sendGroupMessage(message.groupId, "你获得了 $randomPoint 点分数!\n发送‘/我的分数’ 即可查看当前的分数")
    }
}

class MyPointCommand: BaseCommand() {
    override val commandName = "/我的分数"

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val status = signManager.getStatus(message.sender.userId)
        if (status == null) {
            listener.sendGroupMessage(message.groupId, "你还没有签到过呢! 先发送‘/签到’再来查询吧~")
            return
        } else {
            listener.sendGroupMessage(message.groupId, "你当前的分数为: ${status.points}")
        }
    }
}