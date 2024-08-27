/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage
import kotlin.random.Random

class JrrpCommand : BaseCommand() {
    override val commandName = "/今日人品"

    override fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val randomNumber = Random.nextInt(0, 101)
        val scoreDesc = when (randomNumber) {
            in 0..10 -> "人品不太好呢"
            in 30..50 -> "人品一般般"
            in 70..90 -> "人品还不错哦"
            in 90..100 -> "人品超级好呢！"
            else -> "人品还行哦"
        }
        listener.sendGroupMessage(message.groupId, scoreDesc)
    }
}