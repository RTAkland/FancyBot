/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.entity.setu.Setu
import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import java.net.URI

class SetuItem : Item() {
    override val itemNames = listOf("setu", "色图", "st")
    override val itemPrice = 4.0

    override suspend fun redeemInGroup(message: GroupMessage, after: Double): MessageChain.Builder {
        val response = Http.get("$API_RTAST_URL/api/setu")
            .fromArrayJson<List<Setu>>().first()
        if (response.r18) {
            niuziManager.updateLength(message.sender.userId, itemPrice)
            return MessageChain.Builder().addText("不好意思这张图片是R18所以不能发~, 牛子长度已经退还~")
        }
        val imageBase64 = URI(response.urls.large).toURL().readBytes().encodeToBase64()
        val msg = MessageChain.Builder().addImage(imageBase64, true)
        return msg
    }
}