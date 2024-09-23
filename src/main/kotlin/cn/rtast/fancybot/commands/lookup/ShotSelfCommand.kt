/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/23
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.segment.Node
import cn.rtast.rob.segment.PlainText
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener


private fun generateNodeMessage(targetName: String, targetId: String, times: Int = 10): NodeMessageChain {
    val nodeMsg = NodeMessageChain.Builder()
    repeat(times) {
        val node = Node(
            Node.Data(
                targetName,
                targetId,
                listOf(PlainText(PlainText.Data("我是傻逼")))
            )
        )
        nodeMsg.addNode(node)
    }
    return nodeMsg.build()
}

class ShotSelfCommand : BaseCommand() {
    override val commandNames = listOf("骂我")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        listener.sendGroupForwardMsg(
            message.groupId,
            generateNodeMessage(message.sender.nickname, message.sender.userId.toString())
        )
    }
}

class ShotOtherCommand : BaseCommand() {
    override val commandNames = listOf("骂")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`骂 @某人`可以骂他~")
            return
        }
        val target = message.message.find { it.type == ArrayMessageType.at }?.data!!
        listener.sendGroupForwardMsg(message.groupId, generateNodeMessage(target.name!!, target.qq!!))
    }
}