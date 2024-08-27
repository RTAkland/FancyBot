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

class JrrpCommand: BaseCommand() {
    override val commandName = "/jrrp"

    override fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val score = Random.nextInt(0, 101)
        listener.sendGroupMessage(message.groupId, score.toString())
    }
}