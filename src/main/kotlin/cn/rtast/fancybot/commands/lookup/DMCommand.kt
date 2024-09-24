/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.entity.bili.Search
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.parseTimeStamp
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.extractPlainTextFromHtml
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

class DMCommand : BaseCommand() {
    override val commandNames = listOf("/dm")

    private val searchAPIUrl = "https://api.bilibili.com/x/web-interface/wbi/search/all/v2"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val keyword = args.first()
        val response = Http.get<Search>(
            searchAPIUrl, mapOf("keyword" to keyword),
        ).data.result.find { it.resultType == "media_bangumi" }!!
            .data.first()
        val nodeMsg = NodeMessageChain.Builder()
        val msg = MessageChain.Builder()
        val imageBase64 = URI(response.cover).toURL().readBytes().encodeToBase64()
        msg.addImage(imageBase64, true)
            .addText("番名: ${response.title.extractPlainTextFromHtml()}(${response.orgTitle})")
            .addNewLine()
            .addText(response.styles)
            .addNewLine()
            .addText("${response.areas} · ${response.pubTime.parseTimeStamp().year} · ${response.indexShow}")
            .addNewLine()
            .addText(response.desc)
            .addNewLine()
            .addText(response.url)
        nodeMsg.addMessageChain(msg.build(), message.sender.userId)
        listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
    }
}