/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/23
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.onebot.NodeMessageChain
import cn.rtast.rob.util.BaseCommand


private fun generateNodeMessage(targetId: String, times: Int = 10): NodeMessageChain {
    val nodeMsg = NodeMessageChain.Builder()
    repeat(times) {
        val msg = MessageChain.Builder().addText("我是傻逼").build()
        nodeMsg.addMessageChain(msg, targetId.toLong())
    }
    return nodeMsg.build()
}

@CommandDescription("骂自己(好爽)")
class ShotSelfCommand : BaseCommand() {
    override val commandNames = listOf("骂我")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.action.sendGroupForwardMsg(message.groupId, generateNodeMessage(message.sender.userId.toString()))
    }
}

@CommandDescription("骂别人(更爽了")
class ShotOtherCommand : BaseCommand() {
    override val commandNames = listOf("骂")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.action.sendGroupForwardMsg(
                message.groupId, generateNodeMessage(message.sender.userId.toString(), 20)
            )
            return
        }
        var times = if (args.size == 1) 10 else if (args.last() == "") 10 else args.last().toInt()
        if (times >= 200) times = 10
        val target = message.message.find { it.type == ArrayMessageType.at }?.data!!
        message.action.sendGroupForwardMsg(message.groupId, generateNodeMessage(target.qq!!, times))
    }
}