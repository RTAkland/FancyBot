/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/23
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.segment.Image
import cn.rtast.rob.segment.Node
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener

class TenSetuCommand : BaseCommand() {
    override val commandNames = listOf("十张色图")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val nodeImages = mutableListOf<Image>()
        repeat(10) {
            val url = Http.get<Setu>("https://api.rtast.cn/api/setu").urls.large
            nodeImages.add(Image(Image.Data(url)))
        }
        val node = Node(Node.Data("ROBOT", "1845464277", nodeImages))
        val nodeMsg = NodeMessageChain.Builder()
            .addNode(node)
            .build()
        listener.sendGroupForwardMsg(message.groupId, nodeMsg)
    }
}