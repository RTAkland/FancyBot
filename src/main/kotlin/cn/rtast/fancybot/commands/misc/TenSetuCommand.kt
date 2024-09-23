/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/23
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.segment.Node
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

class TenSetuCommand : BaseCommand() {
    override val commandNames = listOf("十张色图")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val nodeMsg = NodeMessageChain.Builder()
        repeat(10) {
            val response = Http.get<Setu>("https://api.rtast.cn/api/setu")
            if (!response.r18) {
                try {
                    val imageBase64 = URI(response.urls.large).toURL().readBytes().encodeToBase64()
                    val tempMsg = MessageChain.Builder().addImage(imageBase64, true).build()
                    val tempNode = Node(
                        Node.Data("", message.userId.toString(), tempMsg.finalArrayMsgList)
                    )
                    nodeMsg.addNode(tempNode)
                } catch (_: Exception) {
                }
            }
        }
        val footerMsg = MessageChain.Builder()
            .addText("图片数量可能不足10张~")
            .addNewLine()
            .addText("因为过滤了R18的图片~")
            .build()
        val footerNode = Node(Node.Data("", message.userId.toString(), footerMsg.finalArrayMsgList))
        nodeMsg.addNode(footerNode)
        listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
    }
}