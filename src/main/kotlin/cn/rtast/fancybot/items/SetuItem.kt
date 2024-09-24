/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

class SetuItem : Item() {
    override val itemNames = listOf("setu", "色图", "st")
    override val itemPrice = 10.0

    override suspend fun redeemInGroup(listener: OneBotListener, message: GroupMessage, after: Double) {
        val response = Http.get("https://api.rtast.cn/api/setu")
            .fromArrayJson<List<Setu>>().first()
        if (response.r18) {
            niuziManager.updateLength(message.sender.userId, itemPrice)
            message.reply("不好意思这张图片是R18所以不能发~, 牛子长度已经退还~")
            return
        }
        val imageBase64 = URI(response.urls.large).toURL().readBytes().encodeToBase64()
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage(imageBase64, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}