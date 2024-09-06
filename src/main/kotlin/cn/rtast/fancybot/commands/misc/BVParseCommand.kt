/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.bili.BVID
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

object BVParseCommand {

    private const val CID_URL = "https://api.bilibili.com/x/web-interface/view"

    suspend fun parse(listener: OBMessage, message: GroupMessage) {
        val bvid = if (message.rawMessage.startsWith("BV")) {
            message.rawMessage
        } else {
            message.rawMessage.split("/")[4]
        }
        val cidResponse = Http.get<BVID>(
            CID_URL, mapOf("bvid" to bvid)
        )
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage(cidResponse.data.pic)
            .addText("标题: ${cidResponse.data.title} | 作者: ${cidResponse.data.owner.name}")
            .addNewLine()
            .addText("点赞: ${cidResponse.data.stat.like} | 观看: ${cidResponse.data.stat.view}")
            .addNewLine()
            .addText("收藏: ${cidResponse.data.stat.favorite} | 投币: ${cidResponse.data.stat.coin}")
            .addNewLine()
            .addText("https://www.bilibili.com/video/$bvid")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}