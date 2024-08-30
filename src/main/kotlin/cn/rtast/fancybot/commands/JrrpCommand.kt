/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.util.file.JrrpManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

class JrrpCommand : BaseCommand() {
    override val commandNames = listOf("/jrrp", "/今日人品")

    private val jrrpManager = JrrpManager()

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (jrrpManager.isJrrped(message.sender.userId)) {
            listener.sendGroupMessage(message.groupId, "你今天已经今日人品过啦, 明天再来吧~")
            return
        }
        val randomPoint = jrrpManager.jrrp(message.sender.userId)
        val scoreDesc = when (randomPoint) {
            in 0..10 -> "人品不太好呢"
            in 30..50 -> "人品一般般"
            in 70..90 -> "人品还不错哦"
            in 90..100 -> "人品超级好呢！"
            else -> "人品还行哦"
        }
        listener.sendGroupMessage(message.groupId, "$scoreDesc($randomPoint)")
    }
}