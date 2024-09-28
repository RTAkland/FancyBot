/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/28
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import cn.rtast.rob.util.ob.asNode
import java.net.URI

class TenSetuItem : Item() {
    override val itemNames = listOf("十张色图")
    override val itemPrice = 20.0

    override suspend fun redeemInGroup(
        listener: OneBotListener,
        message: GroupMessage,
        after: Double
    ): MessageChain.Builder {
        val imageMessages = mutableListOf<MessageChain>()
        val response = Http.get("https://api.rtast.cn/api/setu?size=10").fromArrayJson<List<Setu>>()
        response.filter { !it.r18 }.map { it.urls.large }.forEach {
            try {
                val imageBase64 = URI(it).toURL().readBytes().encodeToBase64()
                val tempMsg = MessageChain.Builder().addImage(imageBase64, true).build()
                imageMessages.add(tempMsg)
            } catch (_: Exception) {
            }
        }
        val footerMsg = MessageChain.Builder()
            .addText("图片数量可能不足10张~")
            .addNewLine()
            .addText("因为过滤了R18的图片~")
            .build()
        imageMessages.add(footerMsg)
        message.reply(imageMessages.asNode(configManager.selfId))
        return MessageChain.Builder()
    }
}