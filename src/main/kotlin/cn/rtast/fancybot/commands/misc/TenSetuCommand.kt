/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/23
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

@CommandDescription("老板你好, 给我来十张色图")
class TenSetuCommand : BaseCommand() {
    override val commandNames = listOf("十张色图")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val nodeMsg = NodeMessageChain.Builder()
        val response = Http.get("https://api.rtast.cn/api/setu?size=10").fromArrayJson<List<Setu>>()
        response.filter { !it.r18 }.map { it.urls.large }.forEach {
            try {
                val imageBase64 = URI(it).toURL().readBytes().encodeToBase64()
                val tempMsg = MessageChain.Builder().addImage(imageBase64, true).build()
                nodeMsg.addMessageChain(tempMsg, configManager.selfId)
            } catch (_: Exception) {
            }
        }
        val footerMsg = MessageChain.Builder()
            .addText("图片数量可能不足10张~")
            .addNewLine()
            .addText("因为过滤了R18的图片~")
            .build()
        nodeMsg.addMessageChain(footerMsg, configManager.selfId)
        listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
    }
}